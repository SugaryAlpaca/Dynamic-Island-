package com.akira.island.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryChargingFull
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.RocketLaunch
import androidx.compose.material.icons.rounded.Security
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.akira.island.core.tr
import com.akira.island.data.SettingsRepo
import com.akira.island.service.KeepAliveService
import com.akira.island.ui.Group
import com.akira.island.ui.GroupItem
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.Page
import com.akira.island.ui.Perms
import com.akira.island.ui.Perms.Vendor
import com.akira.island.ui.Pos
import com.akira.island.ui.Screen
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile

/**
 * «Чтобы остров не закрывался»: Xiaomi (HyperOS), Oppo, realme, OnePlus (ColorOS), Samsung и другие сами
 * выгружают приложения в фоне — особенно если смахнуть их из недавних. Здесь кнопки прямо в нужные
 * настройки этой прошивки и подсказка, как закрепить остров в недавних.
 */
@Composable
fun KeepAliveScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val vendor = Perms.vendor()
    val (battery, _) = rememberOnResume { Perms.batteryUnrestricted(context) }
    val s by SettingsRepo.state.collectAsState()

    Page(tr("Чтобы остров не закрывался", "Manter a ilha ativa"), onBack,
        subtitle = tr("Прошивка телефона может выгружать остров в фоне — тогда он пропадает, пока его не включить снова",
            "O sistema do seu celular pode encerrar a ilha em segundo plano — aí ela some até você ligá-la de novo")) {
        // Процесс со службой на переднем плане Android не закрывает, когда приложение смахивают из недавних
        GroupToggle(
            Pos.Single, Icons.Rounded.PushPin, tr("Не выгружать остров", "Manter a ilha carregada"),
            tr("Остров не пропадёт, даже если смахнуть приложение из недавних. Android может показать тихое уведомление",
                "A ilha continua mesmo se você deslizar o app para fora dos apps recentes. O Android pode mostrar uma notificação silenciosa"),
            s.keepAlive, color = Tile.Purple,
        ) { v ->
            SettingsRepo.update { copy(keepAlive = v) }
            KeepAliveService.sync(context)
        }

        Group {
            GroupItem(
                if (Perms.hasAutostart()) Pos.First else Pos.Single, Icons.Rounded.BatteryChargingFull,
                tr("Батарея без ограничений", "Bateria sem restrições"),
                batteryHint(vendor), onClick = { Perms.openBattery(context) }, color = Tile.Green,
            ) { Status(battery) }
            if (Perms.hasAutostart()) {
                GroupItem(
                    Pos.Last, Icons.Rounded.RocketLaunch, tr("Автозапуск", "Início automático"),
                    tr("Включите для «Динамического острова» — тогда он запустится сам после перезагрузки и закрытия",
                        "Ative para a Ilha Dinâmica — assim ela inicia sozinha após reiniciar o celular ou ser fechada"),
                    onClick = { Perms.openAutostart(context) }, color = Tile.Orange,
                ) { Arrow() }
            }
        }

        SectionHeader(tr("Закрепите в недавних", "Fixe nos apps recentes"))
        InfoText(lockHint(vendor))

        SectionHeader(tr("Если остров всё равно пропал", "Se a ilha ainda assim sumir"))
        Group {
            GroupItem(
                Pos.First, Icons.Rounded.Lock, tr("Спец. возможности", "Acessibilidade"),
                tr("Выключите и снова включите службу «Динамический остров»", "Desligue e ligue de novo o serviço da Ilha Dinâmica"),
                onClick = { Perms.openAccessibility(context) }, color = Tile.Blue,
            ) { Arrow() }
            AppDetailsItem(Pos.Last)
        }
    }
}

/** Пункт «Чтобы остров не закрывался» — в «Разрешениях» и «О приложении». */
@Composable
internal fun KeepAliveItem(pos: Pos, open: (Screen) -> Unit) {
    GroupItem(pos, Icons.Rounded.BatteryChargingFull, tr("Чтобы остров не закрывался", "Manter a ilha ativa"),
        tr("Батарея, автозапуск и закрепление в недавних", "Bateria, início automático e fixação nos apps recentes"),
        onClick = { open(Screen.KEEPALIVE) }, color = Tile.Green) { Arrow() }
}

/** «О приложении» в настройках Android: ограниченные настройки, разрешения, батарея. */
@Composable
internal fun AppDetailsItem(pos: Pos) {
    val context = LocalContext.current
    GroupItem(pos, Icons.Rounded.Security, tr("О приложении в системе", "Informações do app no Android"),
        tr("Ограниченные настройки, разрешения, батарея", "Configurações restritas, permissões, bateria"),
        onClick = { Perms.openAppDetails(context) }, color = Tile.Gray)
}

private fun batteryHint(v: Vendor): String = when (v) {
    Vendor.XIAOMI -> tr("«Контроль активности» или «Экономия энергии» → «Нет ограничений»",
        "Economia de bateria ou “Controle de atividade” → “Sem restrições”")
    Vendor.SAMSUNG -> tr("«Батарея» → «Без ограничений», и уберите остров из «Приложений в спящем режиме»",
        "Bateria → Sem restrições, e tire a ilha de Apps em suspensão")
    Vendor.OPPO -> tr("«Использование батареи» → разрешите работу в фоне", "Uso da bateria → permita atividade em segundo plano")
    else -> tr("Найдите «Динамический остров» и выберите «Не оптимизировать» или «Без ограничений»",
        "Encontre a Ilha Dinâmica e escolha “Não otimizar” ou “Sem restrições”")
}

private fun lockHint(v: Vendor): String = when (v) {
    Vendor.XIAOMI -> tr(
        "Откройте недавние приложения, зажмите карточку «Динамический остров» и нажмите на замок. " +
            "Закреплённое приложение HyperOS и MIUI не закрывают, даже когда вы чистите недавние.",
        "Abra os apps recentes, mantenha o cartão da Ilha Dinâmica pressionado e toque no cadeado. O HyperOS e o MIUI não " +
            "fecham um app fixado, mesmo quando você limpa os apps recentes.",
    )
    Vendor.OPPO -> tr(
        "Откройте недавние приложения, нажмите «⋮» над карточкой «Динамический остров» (или потяните её вниз) " +
            "и выберите «Заблокировать».",
        "Abra os apps recentes, toque em “⋮” acima do cartão da Ilha Dinâmica (ou puxe-o para baixo) e escolha “Bloquear”.",
    )
    Vendor.SAMSUNG -> tr(
        "Откройте недавние приложения, нажмите на значок над карточкой «Динамический остров» и выберите «Не закрывать».",
        "Abra os apps recentes, toque no ícone acima do cartão da Ilha Dinâmica e escolha “Manter aberto”.",
    )
    else -> tr(
        "Откройте недавние приложения и потяните карточку «Динамический остров» вниз или зажмите её — " +
            "появится замок. Закреплённое приложение не закрывается, когда вы чистите недавние.",
        "Abra os apps recentes e puxe o cartão da Ilha Dinâmica para baixo ou mantenha-o pressionado — um cadeado " +
            "aparece. Um app fixado não é fechado quando você limpa os apps recentes.",
    )
}
