/*
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.asciidoctor.gradle.jvm

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.diagnostics.DependencyReportTask
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

@SuppressWarnings(['MethodName', 'DuplicateStringLiteral', 'DuplicateMapLiteral'])
class AsciidoctorJBasePluginSpec extends Specification {

    Project project = ProjectBuilder.builder().build()

    void 'Apply the plugin will add an extension for AsciidoctorJ'() {
        when:
        project.allprojects {
            apply plugin: 'org.asciidoctor.jvm.base'
        }

        AsciidoctorJExtension ext = project.extensions.getByName(AsciidoctorJExtension.NAME)

        then:
        verifyAll {
            ext.version == AsciidoctorJExtension.DEFAULT_ASCIIDOCTORJ_VERSION
            ext.groovyDslVersion == null
            ext.pdfVersion == null
            ext.epubVersion == null
        }
    }

    void 'Apply the plugin will add a report task for AsciidoctorJ'() {
        when:
        project.allprojects {
            apply plugin: 'org.asciidoctor.jvm.base'
        }

        project.evaluate()
        DependencyReportTask task = project.tasks.getByName(AsciidoctorJBasePlugin.DEPS_REPORT)

        then:
        !task.configurations.first().dependencies.empty
    }

    void 'Adding extension will set GroovyDSL'() {
        when:
        project.allprojects {
            apply plugin: 'org.asciidoctor.jvm.base'
        }

        AsciidoctorJExtension ext = project.extensions.getByName(AsciidoctorJExtension.NAME)

        ext.extensions '1'

        then:
        verifyAll {
            ext.version == AsciidoctorJExtension.DEFAULT_ASCIIDOCTORJ_VERSION
            ext.groovyDslVersion == AsciidoctorJExtension.DEFAULT_GROOVYDSL_VERSION
        }
    }

    void 'When the AsciidoctorJ extension is added to a task it defaults values to the one created by the plugin'() {
        when:
        Task taskWithExt
        AsciidoctorJExtension taskExt
        project.allprojects {
            apply plugin: 'org.asciidoctor.jvm.base'
        }

        taskWithExt = project.tasks.create('foo')
        taskExt = taskWithExt.extensions.create(AsciidoctorJExtension.NAME, AsciidoctorJExtension, taskWithExt)
        AsciidoctorJExtension ext = project.extensions.getByName(AsciidoctorJExtension.NAME)

        project.allprojects {
            foo {
                asciidoctorj {
                    version '1.2.3'
                    groovyDslVersion '4.5.6'
                }
            }
        }

        then:
        verifyAll {
            ext.version == AsciidoctorJExtension.DEFAULT_ASCIIDOCTORJ_VERSION
            ext.groovyDslVersion == null
            taskExt.version == '1.2.3'
            taskExt.groovyDslVersion == '4.5.6'
        }

    }
}