package com.enonic.osgi.bundles

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.osgi.OsgiPlugin
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar

class WrapPlugin
    implements Plugin<Project>
{
    private Project project

    private WrapExtension ext

    @Override
    void apply( final Project project )
    {
        this.project = project

        // Add required plugins
        this.project.plugins.apply( JavaPlugin )
        this.project.plugins.apply( OsgiPlugin )
        this.project.plugins.apply( MavenPublishPlugin )

        // Add configurations
        this.project.configurations.create( 'wrap' )
        this.project.configurations.create( 'wrapSources' )

        // Create extension properties
        this.ext = this.project.extensions.create( WrapExtension.NAME, WrapExtension )

        configureBinTasks()
        configureSourcesTasks()

        this.project.afterEvaluate {
            configureDependencies()
        }
    }

    private void configureDependencies()
    {
        this.ext.libs.each {
            configureDependency( it )
        }
    }

    private void configureDependency( final String dep )
    {
        this.project.dependencies.add( 'wrap', dep + '@jar' )
        this.project.dependencies.add( 'wrapSources', dep + ':sources@jar' )
    }

    private void configureBinTasks()
    {
        this.project.task( 'extract', type: Copy ) {
            from {
                this.project.configurations.wrap.collect { this.project.zipTree( it ) }
            }
            into "${this.project.buildDir}/classes/main"
        }

        this.project.tasks.getByName( 'jar' ) {
            dependsOn 'extract'
            manifest this.ext.manifest
        }
    }

    private void configureSourcesTasks()
    {
        this.project.task( 'extractSources', type: Copy ) {
            from {
                this.project.configurations.wrapSources.collect { this.project.zipTree( it ) }
            }
            into "${this.project.buildDir}/sources/main"
        }

        this.project.task( 'sourcesJar', type: Jar, dependsOn: 'extractSources' ) {
            classifier = 'sources'
            from "${this.project.buildDir}/sources/main"
        }

        this.project.with {
            publishing {
                publications {
                    mavenJava( MavenPublication ) {
                        from components.java

                        artifact sourcesJar {
                            classifier "sources"
                        }
                    }
                }
                repositories {
                    maven {
                        url this.ext.repo
                    }
                }
            }
        }
    }
}
