allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

val newBuildDir: Directory =
    rootProject.layout.buildDirectory
        .dir("../../build")
        .get()
rootProject.layout.buildDirectory.value(newBuildDir)

subprojects {
    val newSubprojectBuildDir: Directory = newBuildDir.dir(project.name)
    project.layout.buildDirectory.value(newSubprojectBuildDir)
}

subprojects {
    project.evaluationDependsOn(":app")
    
    // Handle missing namespace in library modules
    afterEvaluate {
        if (plugins.hasPlugin("com.android.library")) {
            val androidExtension = extensions.findByType<com.android.build.gradle.LibraryExtension>()
            if (androidExtension != null) {
                val namespaceValue = androidExtension.namespace.orNull
                if (namespaceValue == null || namespaceValue.isEmpty()) {
                    androidExtension.namespace.set("com.example.${project.name.replace("-", "_")}")
                    println("Set namespace to ${androidExtension.namespace.get()} for ${project.name}")
                }
            }
        }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.layout.buildDirectory)
}
