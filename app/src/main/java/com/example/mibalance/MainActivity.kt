package com.example.mibalance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.mibalance.data.BalanceManager
import com.example.mibalance.data.BalanceWriteResult
import com.example.mibalance.data.ProfileRepository
import com.example.mibalance.model.AudioProfile
import com.example.mibalance.model.HeadphoneType
import com.example.mibalance.ui.components.SimpleHeadphonesIcon
import com.example.mibalance.ui.components.SimpleSlidersIcon
import com.example.mibalance.ui.screens.LiveBalanceScreen
import com.example.mibalance.ui.screens.ProfilesScreen
import com.example.mibalance.ui.theme.MiBalanceTheme
import java.util.UUID

private enum class AppTab(val label: String) {
    PROFILES("Perfiles"),
    BALANCE("Balance")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiBalanceTheme {
                MiBalanceApp()
            }
        }
    }
}

@Composable
private fun MiBalanceApp() {
    val context = LocalContext.current
    val lifecycleOwner = context as? LifecycleOwner
    val repository = remember { ProfileRepository(context) }

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
    val selectedTab = AppTab.entries.getOrElse(selectedTabIndex) { AppTab.PROFILES }
    var profiles by remember { mutableStateOf(repository.loadProfiles()) }
    var activeProfileId by remember { mutableStateOf(repository.loadActiveProfileId()) }
    var currentSystemBalance by remember {
        mutableFloatStateOf(BalanceManager.getCurrentBalance(context))
    }
    var permissionIssue by remember {
        mutableStateOf(BalanceManager.currentIssue(context))
    }

    fun applySystemBalance(value: Float, showToastOnError: Boolean = false): Boolean {
        val result = BalanceManager.applyBalance(context, value)
        return when (result) {
            is BalanceWriteResult.Success -> {
                currentSystemBalance = result.appliedBalance
                permissionIssue = null
                true
            }
            else -> {
                permissionIssue = result
                if (showToastOnError) {
                    Toast.makeText(
                        context,
                        "No se pudo aplicar el balance",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                false
            }
        }
    }

    fun refreshBalance() {
        currentSystemBalance = BalanceManager.getCurrentBalance(context)
        permissionIssue = BalanceManager.start(context)
    }

    DisposableEffect(lifecycleOwner) {
        permissionIssue = BalanceManager.start(context)
        val owner = lifecycleOwner
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshBalance()
            }
        }
        owner?.lifecycle?.addObserver(observer)
        onDispose {
            owner?.lifecycle?.removeObserver(observer)
            BalanceManager.stop()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    icon = {
                        SimpleHeadphonesIcon(
                            tint = if (selectedTab == AppTab.PROFILES) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text(AppTab.PROFILES.label) }
                )
                NavigationBarItem(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    icon = {
                        SimpleSlidersIcon(
                            tint = if (selectedTab == AppTab.BALANCE) {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = { Text(AppTab.BALANCE.label) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.PROFILES -> {
                ProfilesScreen(
                    profiles = profiles,
                    activeProfileId = activeProfileId,
                    currentSystemBalance = currentSystemBalance,
                    permissionIssue = permissionIssue,
                    onRetryPermission = {
                        val ok = applySystemBalance(currentSystemBalance, showToastOnError = false)
                        if (ok) {
                            Toast.makeText(context, "Permiso correcto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onSelectProfile = { profile ->
                        if (activeProfileId == profile.id) {
                            // Segunda pulsación sobre el perfil activo: se deselecciona
                            activeProfileId = null
                            repository.saveActiveProfileId(null)
                            Toast.makeText(
                                context,
                                "Perfil desactivado (el balance queda como está)",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            activeProfileId = profile.id
                            repository.saveActiveProfileId(profile.id)
                            val applied = applySystemBalance(profile.balance, showToastOnError = true)
                            if (applied) {
                                Toast.makeText(
                                    context,
                                    "${profile.name}: ${BalanceManager.formatShortBadge(profile.balance)}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    onSaveProfile = { existingId, name, type, balance ->
                        val normalized = BalanceManager.normalizeBalance(balance)
                        val updatedList = if (existingId == null) {
                            val newProfile = AudioProfile(
                                id = UUID.randomUUID().toString(),
                                name = name,
                                headphoneType = type,
                                balance = normalized
                            )
                            activeProfileId = newProfile.id
                            repository.saveActiveProfileId(newProfile.id)
                            applySystemBalance(normalized)
                            profiles + newProfile
                        } else {
                            profiles.map {
                                if (it.id == existingId) {
                                    it.copy(name = name, headphoneType = type, balance = normalized)
                                } else {
                                    it
                                }
                            }.also {
                                if (activeProfileId == existingId) {
                                    applySystemBalance(normalized)
                                }
                            }
                        }
                        profiles = updatedList
                        repository.saveProfiles(updatedList)
                    },
                    onDeleteProfile = { profile ->
                        val updated = profiles.filterNot { it.id == profile.id }
                        profiles = updated
                        repository.saveProfiles(updated)
                        if (activeProfileId == profile.id) {
                            activeProfileId = null
                            repository.saveActiveProfileId(null)
                        }
                    },
                    onLivePreviewBalance = { value ->
                        applySystemBalance(value)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppTab.BALANCE -> {
                LiveBalanceScreen(
                    currentBalance = currentSystemBalance,
                    permissionIssue = permissionIssue,
                    onRetryPermission = {
                        val ok = applySystemBalance(currentSystemBalance, showToastOnError = false)
                        if (ok) {
                            Toast.makeText(context, "Permiso correcto", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBalanceChange = { newValue ->
                        currentSystemBalance = newValue
                        activeProfileId = null
                        repository.saveActiveProfileId(null)
                        applySystemBalance(newValue)
                    },
                    onResetBalance = {
                        currentSystemBalance = 0f
                        activeProfileId = null
                        repository.saveActiveProfileId(null)
                        applySystemBalance(0f, showToastOnError = true)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
