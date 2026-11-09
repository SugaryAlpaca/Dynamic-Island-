package com.akira.island.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PersistableBundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akira.island.R
import com.akira.island.core.Tints
import com.akira.island.core.tr
import com.akira.island.data.SettingsRepo
import com.akira.island.service.LiveDebug
import com.akira.island.service.PayDebug
import com.akira.island.ui.Group
import com.akira.island.ui.GroupItem
import com.akira.island.ui.GroupToggle
import com.akira.island.ui.NotesDialog
import com.akira.island.ui.Page
import com.akira.island.ui.Perms
import com.akira.island.ui.PillAction
import com.akira.island.ui.Pos
import com.akira.island.ui.Screen
import com.akira.island.ui.SectionHeader
import com.akira.island.ui.Tile
import com.akira.island.ui.Ui
import com.akira.island.ui.UpdateSection
import com.akira.island.ui.updateStatusText
import com.akira.island.update.UpdateChecker
import com.akira.island.update.UpdateState
import kotlinx.coroutines.launch

@Composable
fun InstructionScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    Page(tr("Инструкция", "Instruções"), onBack) {
        Step(1, tr("Спец. возможности", "Acessibilidade"),
            tr(
                "Откройте «Настройки → Спец. возможности → Установленные приложения → Динамический остров» и включите службу. " +
                    "Только такая служба может рисовать поверх строки состояния, рядом с камерой.",
                "Abra Configurações → Acessibilidade → Apps instalados → Ilha Dinâmica e ative o serviço. Só um " +
                    "serviço assim pode desenhar sobre a barra de status, ao lado da câmera.",
            ))
        Step(2, tr("Если переключатель серый (Android 13+)", "Se a chave estiver cinza (Android 13+)"), restrictedHelp())
        Row { PillAction(tr("Открыть «О приложении»", "Abrir Informações do app")) { Perms.openAppDetails(context) } }
        Step(3, tr("Доступ к уведомлениям", "Acesso às notificações"),
            tr(
                "Нужен для карточек музыки, прогресса загрузок и таймеров/секундомеров Google Часов. " +
                    "Остров не хранит и никуда не отправляет уведомления.",
                "Necessário para os cartões de música, progresso de downloads e timer e cronômetro do Google Relógio. A ilha não " +
                    "guarda nem envia notificações para lugar nenhum.",
            ))
        Row { PillAction(tr("Открыть доступ к уведомлениям", "Abrir acesso às notificações")) { Perms.openNotifications(context) } }
        Step(4, tr("Звонки", "Chamadas"),
            tr(
                "На вкладке «Главная» откройте «Разрешения» → «Звонки» и разрешите доступ к телефону, журналу звонков и контактам. " +
                    "Тогда остров покажет входящий вызов с любой звонилкой, подпишет имя из контактов, " +
                    "а кнопки «ответить» и «сбросить» будут работать.",
                "Na aba Início, abra Permissões → Chamadas e permita o acesso ao telefone, ao registro de chamadas e aos contatos. Assim a " +
                    "ilha mostra as chamadas recebidas com qualquer app de telefone, exibe o nome dos seus contatos, e os " +
                    "botões de atender e recusar funcionam.",
            ))
        Step(5, tr("Поставьте капсулу над камерой", "Posicione a cápsula sobre a câmera"),
            tr(
                "Вкладка «Оформление» → «Положение и размер». Пока экран открыт, остров красный: " +
                    "сдвиньте его, пока красное не окажется ровно над камерой, подберите ширину и высоту и нажмите «Сохранить».",
                "Aba Aparência → Posição e tamanho. Enquanto a tela estiver aberta, a ilha fica vermelha: mova-a até o vermelho ficar " +
                    "exatamente sobre a câmera, ajuste a largura e a altura e toque em “Salvar”.",
            ))
        Step(6, tr("Батарея (Xiaomi, Huawei, Samsung…)", "Bateria (Xiaomi, Huawei, Samsung…)"),
            tr(
                "Если остров пропадает, разрешите приложению автозапуск и снимите ограничения батареи — " +
                    "иначе прошивка выгружает службу.",
                "Se a ilha sumir, permita o início automático do app e remova as restrições de bateria — senão o " +
                    "sistema encerra o serviço.",
            ))
        Row {
            PillAction(tr("Батарея", "Bateria")) { Perms.openBattery(context) }
            if (Perms.hasAutostart()) {
                Spacer(Modifier.width(8.dp))
                PillAction(tr("Автозапуск", "Início automático")) { Perms.openAutostart(context) }
            }
        }
        Step(7, tr("Ответ на сообщения и оплата", "Respostas e pagamentos"),
            tr(
                "Под уведомлением от мессенджера стоят кнопки из самого уведомления. «Ответить» открывает строку ввода прямо в острове. " +
                    "Когда приложение оплаты показывает экран оплаты телефоном, остров показывает «К терминалу», а после оплаты — галочку с суммой. " +
                    "Всё это можно попробовать: «Главная» → «Проверка».",
                "Sob a notificação de um mensageiro ficam os próprios botões da notificação. “Responder” abre um campo de texto direto" +
                    " na ilha. Quando um app de pagamento mostra a tela de pagamento por aproximação, a ilha mostra “Aproxime do leitor”, " +
                    "e depois de pagar — um ícone de confirmação com o valor. Você pode testar tudo isso: Início → Testar.",
            ))
        Step(8, tr("Навигатор", "Navegação"),
            tr(
                "Пока открыт навигатор (2ГИС, Яндекс Навигатор, Карты и др.), остров читает с его экрана расстояние до манёвра, " +
                    "улицу и время прибытия, а стрелку поворота узнаёт по картинке. Экран других приложений не читается. " +
                    "Если после обновления остров пропал, выключите и снова включите его в спец. возможностях.",
                "Enquanto um app de navegação estiver aberto (2GIS, Yandex Navigator, Maps e outros), a ilha lê na tela dele a distância " +
                    "até a próxima curva, a rua e o horário de chegada, e reconhece a seta da curva pela" +
                    " imagem. A tela de outros apps não é lida. Se a ilha sumir depois de uma atualização, desative e ative " +
                    "de novo em Acessibilidade.",
            ))
        Step(9, tr("Жесты", "Gestos"),
            tr(
                "Нажатие — раскрыть, потянуть вниз — раскрыть, вверх или касание вне острова — свернуть, " +
                    "толкнуть вбок — следующий/предыдущий трек, нажатие на пузырь — раскрыть вторую активность.",
                "Toque — expande, puxar para baixo — expande, puxar para cima ou tocar fora da ilha — recolhe, empurrar para o lado — próxima ou" +
                    " faixa anterior, toque na bolha — expande a segunda atividade.",
            ))
    }
}

@Composable
private fun Step(n: Int, title: String, text: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Ui.Card).padding(18.dp),
    ) {
        Box(Modifier.size(34.dp).clip(CircleShape).background(Ui.Primary), contentAlignment = Alignment.Center) {
            Text("$n", color = Ui.OnPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, color = Ui.Text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(4.dp))
            Text(text, color = Ui.Sub, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

private val GithubPurple = Color(0xFF8250DF)

/** Главная страница проекта на GitHub — в браузере или приложении GitHub. */
private fun openGithub(context: Context) {
    runCatching {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/${UpdateChecker.REPO}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        )
    }
}

/** «О приложении»: версия и обновления, помощь и ссылки — отдельно от настроек. */
@Composable
fun AppInfoScreen(open: (Screen) -> Unit) {
    val context = LocalContext.current
    val s by SettingsRepo.state.collectAsState()
    val update by UpdateChecker.state.collectAsState()
    val scope = rememberCoroutineScope()
    val version = remember { UpdateChecker.currentVersion(context) }
    var notes by remember { mutableStateOf(false) }

    Page(tr("О приложении", "Sobre")) {
        AppHeader(version, update)
        UpdateSection()

        SectionHeader(tr("Обновления", "Atualizações"))
        Group {
            GroupToggle(Pos.First, Icons.Rounded.SystemUpdate, tr("Искать обновления", "Verificar atualizações"),
                tr("Раз в 3 часа смотреть на GitHub, не вышла ли новая версия", "Procurar no GitHub a cada 3 horas se saiu uma versão nova"),
                s.autoUpdateCheck, color = Tile.Green) { v ->
                SettingsRepo.update { copy(autoUpdateCheck = v) }
            }
            GroupItem(Pos.Middle, Icons.Rounded.Refresh, tr("Проверить сейчас", "Verificar agora"), updateStatusText(update, version),
                onClick = { scope.launch { UpdateChecker.check(context.applicationContext, force = true) } }, color = Tile.Blue)
            GroupItem(Pos.Last, Icons.Rounded.NewReleases, tr("Что нового", "Novidades"), tr("Изменения в версии $version", "Mudanças na versão $version"),
                onClick = { notes = true }, color = Tile.Purple) { Arrow() }
        }

        SectionHeader(tr("Помощь", "Ajuda"))
        Group {
            GroupItem(Pos.First, Icons.Rounded.Lightbulb, tr("Инструкция", "Instruções"),
                tr("Как включить остров на Android 13+", "Como ligar a ilha no Android 13+"),
                onClick = { open(Screen.INSTRUCTION) }, color = Tile.Yellow) { Arrow() }
            KeepAliveItem(Pos.Middle, open)
            GroupItem(Pos.Middle, Icons.Rounded.Navigation, tr("Диагностика навигации", "Diagnóstico de navegação"),
                tr("Что присылает навигатор, такси или доставка — если не видно расстояния",
                    "O que um app de navegação, táxi ou entrega envia — se a distância não aparece"),
                onClick = { open(Screen.DIAG) }, color = Tile.Teal) { Arrow() }
            GroupItem(Pos.Middle, Icons.Rounded.CreditCard, tr("Диагностика оплаты", "Diagnóstico de pagamento"),
                tr("Что остров увидел при оплате — если «Оплачено» не появилось", "O que a ilha viu quando você pagou — se “Pago” não apareceu"),
                onClick = { open(Screen.PAYDIAG) }, color = Tile.Green) { Arrow() }
            AppDetailsItem(Pos.Last)
        }

        SectionHeader(tr("Проект", "Projeto"))
        Group {
            GroupItem(Pos.First, ImageVector.vectorResource(R.drawable.ic_github), tr("Страница GitHub", "Página do GitHub"),
                tr("Описание, новые версии и исходный код", "Descrição, novas versões e código-fonte"),
                onClick = { openGithub(context) }, color = GithubPurple) { Arrow() }
            GroupItem(Pos.Last, Icons.Rounded.Info, tr("Об острове", "Sobre a ilha"),
                tr("Что умеет и откуда идея", "O que ela faz e de onde veio a ideia"),
                onClick = { open(Screen.ABOUT) }, color = Tile.Indigo) { Arrow() }
        }
    }

    if (notes) NotesDialog { notes = false }
}

/** Шапка «О приложении»: значок, название, версия и есть ли обновление. */
@Composable
private fun AppHeader(version: String, update: UpdateState) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(Ui.Card).padding(vertical = 26.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Как значок приложения на рабочем столе: капсула на сиреневом
        Box(Modifier.size(84.dp).clip(RoundedCornerShape(24.dp)).background(Ui.Primary), contentAlignment = Alignment.Center) {
            Image(ImageVector.vectorResource(R.drawable.ic_launcher_foreground), null, Modifier.requiredSize(126.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(tr("Динамический остров", "Ilha Dinâmica"), color = Ui.Text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(tr("Версия $version", "Versão $version"), color = Ui.Sub, fontSize = 15.sp, modifier = Modifier.padding(top = 2.dp))
        val (label, color) = when (update) {
            UpdateState.UpToDate -> tr("Последняя версия", "Atualizado") to Tints.Green
            is UpdateState.Available -> tr("Доступна версия ${update.release.version}", "A versão ${update.release.version} está disponível") to Ui.Primary
            UpdateState.Checking -> tr("Проверяю обновления…", "Verificando atualizações…") to Ui.Sub
            is UpdateState.Error -> tr("Не удалось проверить обновления", "Não foi possível verificar atualizações") to Tints.Orange
            UpdateState.Idle -> null to Ui.Sub
        }
        if (label != null) {
            Text(
                label, color = color, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp).clip(CircleShape).background(color.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            )
        }
    }
}

/** «Об острове»: что он умеет, откуда идея и как устроен. */
@Composable
fun IdeaScreen(onBack: () -> Unit) {
    Page(tr("Об острове", "Sobre a ilha"), onBack) {
        IslandPreview()
        SectionHeader(tr("Что умеет", "O que ela faz"))
        InfoText(
            tr(
                "Капсула в духе iPhone над камерой Android: музыка с обложкой, эквалайзером и синхронным текстом песен, " +
                    "таймер с кольцом остатка, секундомер, прогресс загрузок, фонарик с регулировкой яркости, " +
                    "уведомления с ответом, оплата, навигация, зарядка и системные события. Вторая активность уходит в пузырь рядом с островом.",
                "Uma cápsula no estilo iPhone sobre a câmera do seu Android: música com capa, equalizador e letra sincronizada, " +
                    "timer com anel de progresso, cronômetro, progresso de downloads, lanterna com controle de brilho, " +
                    "notificações com resposta, pagamentos, navegação, carregamento e eventos do sistema. Uma segunda atividade vai para" +
                    " uma bolha ao lado da ilha.",
            ),
        )
        SectionHeader(tr("Откуда идея", "De onde veio a ideia"))
        InfoText(
            tr(
                "Идея и поведение перенесены с ПК-версии Dynamic Island для Windows (github.com/mihailkotovski/DynamicIsland): " +
                    "пружинные анимации формы, плеер по клику, толчок вбок для смены трека, таймер от 1 до 99 минут " +
                    "с кольцом вокруг паузы и звонком, текст песен с LRCLIB.",
                "A ideia e o comportamento vêm da versão para PC, Dynamic Island para Windows " +
                    "(github.com/mihailkotovski/DynamicIsland): animações de forma com efeito de mola, o player ao clicar, um empurrão para o lado " +
                    "para trocar de faixa, timer de 1 a 99 minutos com um anel em volta da pausa e um alarme, letras do LRCLIB.",
            ),
        )
        SectionHeader(tr("Как устроено", "Como funciona"))
        InfoText(
            tr(
                "Капсула рисуется службой спец. возможностей, данные о музыке берутся из системных медиасессий, " +
                    "прогресс и таймеры — из уведомлений. Анимации идут, только пока капсула что-то показывает; " +
                    "«Уменьшить эффекты» выключает эквалайзер и «желе».",
                "A cápsula é desenhada por um serviço de acessibilidade, os dados de música vêm das sessões de mídia do sistema, " +
                    "e o progresso e os timers vêm das notificações. As animações rodam só enquanto a cápsula mostra algo; Reduzir " +
                    "efeitos desliga o equalizador e o efeito de gelatina.",
            ),
        )
    }
}

@Composable
internal fun InfoText(text: String) {
    Text(
        text, color = Ui.Sub, fontSize = 14.sp, lineHeight = 20.sp,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Ui.Card).padding(20.dp),
    )
}

/**
 * Что остров видит в уведомлениях навигаторов, такси и доставки: если расстояние или стрелка
 * не показываются, здесь видно, что именно присылает приложение.
 */
@Composable
fun DiagScreen(onBack: () -> Unit) {
    val dumps by LiveDebug.dumps.collectAsState()
    DumpScreen(
        tr("Диагностика навигации", "Diagnóstico de navegação"),
        tr(
            "Включите навигатор, такси или доставку и вернитесь сюда: ниже то, что приложение присылает в уведомлении. " +
                "Если расстояние или стрелка поворота не показываются в острове, нажмите «Скопировать» и пришлите этот текст.",
            "Abra um app de navegação, táxi ou entrega e volte aqui: abaixo está o que o app envia na " +
                "notificação. Se a distância ou a seta da curva não aparecer na ilha, toque em “Copiar tudo” e envie este " +
                "texto.",
        ),
        tr("Пока ничего нет: ни одного уведомления навигатора, такси или доставки не пришло.",
            "Nada por enquanto: nenhuma notificação de navegação, táxi ou entrega chegou."),
        dumps.values.toList(), onBack,
    )
}

/**
 * Что остров увидел при оплате: экран оплаты, уведомления и СМС банков и как он их понял. Если «Оплачено»
 * не появилось, здесь видно почему.
 */
@Composable
fun PayDiagScreen(onBack: () -> Unit) {
    val log by PayDebug.log.collectAsState()
    DumpScreen(
        tr("Диагностика оплаты", "Diagnóstico de pagamento"),
        tr(
            "Заплатите телефоном и вернитесь сюда: ниже — что остров увидел (экран оплаты, уведомление или СМС банка) " +
                "и как понял. Если «Оплачено» не появилось, нажмите «Скопировать» и пришлите текст. Записи хранятся " +
                "только в памяти телефона и пропадают после перезапуска.",
            "Pague com o celular e volte aqui: abaixo está o que a ilha viu (a tela de pagamento, uma notificação " +
                "ou SMS do banco) e como ela interpretou. Se “Pago” não apareceu, toque em “Copiar tudo” e envie o texto. " +
                "Os registros ficam só na memória do celular e somem após reiniciar.",
        ),
        tr("Пока ничего нет: остров не видел ни экрана оплаты, ни уведомлений банков.",
            "Nada por enquanto: a ilha não viu nenhuma tela de pagamento nem notificação de banco."),
        log, onBack,
    )
}

@Composable
private fun DumpScreen(title: String, intro: String, empty: String, items: List<String>, onBack: () -> Unit) {
    val context = LocalContext.current
    Page(title, onBack) {
        InfoText(intro)
        if (items.isEmpty()) {
            Text(empty, color = Ui.Sub, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 8.dp))
        } else {
            Row {
                PillAction(tr("Скопировать всё", "Copiar tudo")) {
                    // Суммы и тексты банков: Android 13+ не показывает такой буфер обмена во всплывающем превью
                    val clip = ClipData.newPlainText(title, items.joinToString("\n\n"))
                    clip.description.extras = PersistableBundle().apply { putBoolean("android.content.extra.IS_SENSITIVE", true) }
                    context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(clip)
                }
            }
            items.forEach { dump ->
                Text(
                    dump, color = Ui.Text, fontSize = 12.sp, lineHeight = 17.sp, fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Ui.Card).padding(16.dp),
                )
            }
        }
    }
}
