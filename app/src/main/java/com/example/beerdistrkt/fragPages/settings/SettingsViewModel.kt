package com.example.beerdistrkt.fragPages.settings

import com.example.beerdistrkt.BaseViewModel

class SettingsViewModel: BaseViewModel() {

    fun setNewValue(code: SettingCode, value: String) {


    }

    enum class SettingCode(code: String) {
        IDLE_WARNING("customer_idle_warning"),
        CLEANING_WARNING("object_cleaning_warning")
    }
}