package com.example

import com.example.data.DefaultData
import com.example.model.AudioDeviceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun audioPreset_bandLevelsParsing_isCorrect() {
        val preset = DefaultData.SYSTEM_PRESETS[1] // Studio Master
        val bands = preset.getBandLevels()
        assertEquals(10, bands.size)
        assertEquals(4, bands[0])
    }

    @Test
    fun defaultData_systemPresets_arePresent() {
        assertTrue(DefaultData.SYSTEM_PRESETS.isNotEmpty())
        val flat = DefaultData.SYSTEM_PRESETS.find { it.id == "preset_flat" }
        assertNotNull(flat)
        assertEquals(0, flat?.bassBoostStrength)
    }

    @Test
    fun defaultData_profiles_containSupportedDevices() {
        val headphones = DefaultData.DEFAULT_PROFILES.find { it.deviceType == AudioDeviceType.HEADPHONES.name }
        assertNotNull(headphones)
        assertTrue(headphones?.autoActivate == true)
    }
}
