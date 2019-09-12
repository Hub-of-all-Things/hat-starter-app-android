object Modules {
    val login = ":login"
    val signup = ":signup"
}

object Versions {
    val kotlin = "1.3.50"
    val hatApi = "0.1.4.2"
    val glide = "4.8.0"
    val recyclerview = "1.0.0"
    val constraint = "1.1.3"
    val junit = "4.12"
    val fuel = "1.14.0'"
    val jackson = "2.9.4.1"
}

object Libraries {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val jwt = "com.nimbusds:nimbus-jose-jwt:5.8"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val fuel = "com.github.kittinunf.fuel:fuel-android:${Versions.fuel}"
    val jackson = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.jackson}"
}

object HATApiLibrary {
    val api = "com.hubofallthings.android.hatApi:hat:${Versions.hatApi}"
}

object SupportLibraries {
    val recyclerview = "androidx.recyclerview:recyclerview:${Versions.recyclerview}"
    val constraint = "com.android.support.constraint:constraint-layout:${Versions.constraint}"
}

object TestLibraries {
    val junit = "junit:junit:${Versions.junit}"
}