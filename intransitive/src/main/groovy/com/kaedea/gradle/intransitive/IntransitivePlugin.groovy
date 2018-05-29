/*
 * Copyright (c) 2018. Kaede <kidhaibara@gmail.com>.
 *
 */

package com.kaedea.gradle.intransitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies

class IntransitivePlugin implements Plugin<Project> {

    private static final String PLUGIN_EXT_CONFIG = "intransitives"

    @Override
    void apply(Project project) {
        project.extensions.create(PLUGIN_EXT_CONFIG, IntransitivePluginExtension.class)

        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            void beforeResolve(ResolvableDependencies resolvableDependencies) {
                project.logger.lifecycle "========================"
                project.logger.lifecycle "Intransitive plugin DependencyResolutionListener#beforeResolve"
                project.logger.info "Dumping deps info, = ${project.extensions[PLUGIN_EXT_CONFIG].deps}"
                project.extensions[PLUGIN_EXT_CONFIG].deps.each { addDependency(project, it) }
                project.getGradle().removeListener(this)
            }

            @Override
            void afterResolve(ResolvableDependencies resolvableDependencies) {}
        })
    }

    private void addDependency(def project, def dep) {
        // Dependency for compilation.
        // Hide transitive dependencies from client.
        project.configurations['compile'].dependencies.add(project.dependencies.create(dep) {
            transitive = false
        })
        // Dependency for package.
        // Need all transitive dependencies in runtime.
        project.configurations['runtimeOnly'].dependencies.add(project.dependencies.create(dep) {
            transitive = true
        })
    }
}

class IntransitivePluginExtension {
    Set<String> deps = new HashSet<>()

    // Use as the following
    // dsl {
    //     compile 'dependency 1'
    //     compile 'dependency 2'
    // }
    void compile(def dep) {
        deps << dep
    }
}