pluginManagement {
    fun Settings.getEnv(key: String,): String? = providers.environmentVariable(key).orNull
    val catalogVersion: String by settings
    val gamCatalogVersion: String? by settings
    val internalCatalogVersion: String? by settings
    val pluginsVersion: String? by settings
    val frameworkCatalogVersion: String? by settings
    val businessCatalogVersion: String? by settings


    val artifactoryUrl = getEnv("NEXUS_URL")
    val artifactoryUsername = getEnv("NEXUS_USER")
    val artifactoryPassword = getEnv("NEXUS_PASSWORD")
    val isCI = getEnv("CI")
    val isRelease = getEnv("RELEASE")

    val repo = getEnv("NEXUS_REPO")
    val repoDev = getEnv("NEXUS_REPO_DEV")
    val mvn = getEnv("NEXUS_MVN_ALL")

    val artifactoryRepoKey = if (isRelease == "true") repo else repoDev
    val repoUrl = "$artifactoryUrl/$artifactoryRepoKey"
    val mvnUrl = "$artifactoryUrl/$mvn"

    val cacheRepo = getEnv("NEXUS_CACHE")
    val cacheUrl = "$artifactoryUrl/$cacheRepo/"

    println("CI Build?: $isCI")
    println("Is Release?: $isRelease")
//    fun RepositoryHandler.mavenWithCredentials(url: String) {
//        maven {
//            setUrl(url)
//            credentials(PasswordCredentials::class) {
//                username = artifactoryUsername
//                password = artifactoryPassword
//            }
//        }
//    }
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
//        mavenWithCredentials(repoUrl)
//        mavenWithCredentials(mvnUrl)
        mavenCentral()
        jcenter()
        maven { setUrl("https://jitpack.io")}

        gradlePluginPortal()
    }
    dependencyResolutionManagement {


        repositories {
            google()
            repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//            mavenWithCredentials(repoUrl)
//            mavenWithCredentials(mvnUrl)
            gradlePluginPortal()
            jcenter()
            mavenCentral()
            maven { setUrl("https://jitpack.io")}

        }
    }


}

rootProject.name = "KotlinCorountine"
include(":app")
 