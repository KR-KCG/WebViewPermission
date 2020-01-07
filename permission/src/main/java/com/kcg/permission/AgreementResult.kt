package com.kcg.permission

interface AgreementResult {
    fun result(termsOfUse: Boolean, personalInformationCollection: Boolean, marketing: Boolean)
}