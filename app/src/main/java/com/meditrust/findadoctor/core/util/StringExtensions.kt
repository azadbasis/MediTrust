package com.meditrust.findadoctor.core.util

import android.util.Log

fun String.isValidDomain(domainName: String): Boolean {
    Log.d("StringExtension", "isValidDomain: verifying email has correct domain: $this")
    val domain = this.substring(this.indexOf("@") + 1).lowercase()
    Log.d("StringExtension", "isValidDomain: user's domain: $domain")
    return domain == domainName
}
fun String.isValidEmail(): Boolean {
    Log.d("StringExtension", "isValidEmail: verifying email format: $this")
    val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    val isValid = this.matches(emailPattern.toRegex())
    Log.d("StringExtension", "isValidEmail: email is valid: $isValid")
    return isValid
}
