/*
 * Copyright (c) 2018. Kaede <kidhaibara@gmail.com>.
 *
 */

package com.kaedea.gradle.intransitive

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.tasks.TaskAction


class IntransitivePlugin implements Plugin<Project> {

    static final String PLUGIN_EXT_CONFIG = "intransitives"
    static final String PLUGIN_TASK_DUMP = "intransitives"

    @Override
    void apply(Project project) {
        project.extensions.create(PLUGIN_EXT_CONFIG, IntransitiveExtension.class)
        project.task(PLUGIN_TASK_DUMP, type: IntransitiveTask)

        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                project.logger.lifecycle "========================"
                project.logger.lifecycle "Intransitive plugin DependencyResolutionListener#beforeResolve"
                project.logger.info "Dumping deps info" +
                        ", compile = ${project.extensions[PLUGIN_EXT_CONFIG].compiles}" +
                        ", api = ${project.extensions[PLUGIN_EXT_CONFIG].apis}" +
                        ", implementation = ${project.extensions[PLUGIN_EXT_CONFIG].implementations}"

                project.extensions[PLUGIN_EXT_CONFIG].compiles.each {
                    addDependency(project, 'compile', it)
                }
                project.extensions[PLUGIN_EXT_CONFIG].apis.each {
                    addDependency(project, 'api', it)
                }
                project.extensions[PLUGIN_EXT_CONFIG].implementations.each {
                    addDependency(project, 'implementation', it)
                }
                project.getGradle().removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {}
        })
    }

    private void addDependency(def project, def configuration, def dep) {
        // Dependency for compilation.
        // Hide transitive dependencies from client.
        project.configurations[(configuration)].dependencies.add(project.dependencies.create(dep) {
            transitive = false
        })
        // Dependency for package.
        // Need all transitive dependencies in runtime.
        project.configurations['runtimeOnly'].dependencies.add(project.dependencies.create(dep) {
            transitive = true
        })
    }
}

class IntransitiveExtension {

    Set<String> compiles = new HashSet<>()
    Set<String> apis = new HashSet<>()
    Set<String> implementations = new HashSet<>()

    // Use as the following
    // dsl {
    //     compile 'dependency 1'
    //     compile 'dependency 2'
    // }
    void compile(def dep) {
        compiles << dep
    }

    // Use as the following
    // dsl {
    //     api 'dependency 1'
    //     api 'dependency 2'
    // }
    void api(def dep) {
        apis << dep
    }

    // Use as the following
    // dsl {
    //     implementation 'dependency 1'
    //     implementation 'dependency 2'
    // }
    void implementation(def dep) {
        implementations << dep
    }
}

class IntransitiveTask extends DefaultTask {

    @TaskAction
    void dump() {
        project.logger.lifecycle "Dumping intransitives of [$project.name]:"
        project.logger.info "compile =  ${project.extensions[IntransitivePlugin.PLUGIN_EXT_CONFIG].compiles}"
        project.logger.info "api =  ${project.extensions[IntransitivePlugin.PLUGIN_EXT_CONFIG].apis}"
        project.logger.info "implementation =  ${project.extensions[IntransitivePlugin.PLUGIN_EXT_CONFIG].implementations}"
    }
}