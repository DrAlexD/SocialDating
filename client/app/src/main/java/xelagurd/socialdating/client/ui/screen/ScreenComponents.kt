package xelagurd.socialdating.client.ui.screen

import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import xelagurd.socialdating.client.R
import xelagurd.socialdating.client.data.model.DataEntity
import xelagurd.socialdating.client.ui.form.FormFieldError

@Composable
@ReadOnlyComposable
fun stringResourceWithColon(@StringRes id: Int) =
    stringResource(R.string.text_with_colon, stringResource(id))

@Composable
fun AppLoadingIndicator(
    modifier: Modifier = Modifier
) {
    CircularProgressIndicator(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_16dp))
            .testTag(stringResource(R.string.loading))
    )
}

@Composable
fun AppLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = progress,
        drawStopIndicator = {},
        modifier = modifier.testTag(stringResource(R.string.progress_indicator))
    )
}

@Composable
fun AppSmallBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_1dp))
    )
}

@Composable
fun AppMediumBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_2dp))
    )
}

@Composable
fun AppLargeBodyText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_4dp))
    )
}

@Composable
fun AppSmallTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = overrideModifier
            ?: modifier.padding(dimensionResource(R.dimen.padding_4dp))
    )
}

@Composable
fun AppMediumTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp))
    )
}

@Composable
fun AppLargeTitleText(
    text: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_12dp))
    )
}

@Composable
fun AppMediumTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    isEnabled: Boolean = true,
    isHasBorder: Boolean = false
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_2dp)),
        isHasBorder = isHasBorder,
        modifier = modifier,
        overrideModifier = overrideModifier
    ) {
        AppMediumTitleText(text = text)
    }
}

@Composable
fun AppLargeTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    isEnabled: Boolean = true,
    isHasBorder: Boolean = false
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        isHasBorder = isHasBorder,
        modifier = modifier,
        overrideModifier = overrideModifier
    ) {
        AppLargeTitleText(text = text)
    }
}

@Composable
private fun AppTextCard(
    isEnabled: Boolean,
    onClick: () -> Unit,
    elevation: CardElevation,
    isHasBorder: Boolean,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        enabled = isEnabled,
        onClick = onClick,
        elevation = elevation,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary).takeIf { isHasBorder },
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = content
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    overrideModifier: Modifier? = null,
    textModifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    error: FormFieldError? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            AppMediumTitleText(
                text = label,
                overrideModifier = textModifier
            )
        },
        isError = error != null,
        supportingText = error?.let {
            {
                AppSmallBodyText(
                    text = stringResource(it.messageRes, *it.formatArgs.toTypedArray())
                )
            }
        },
        singleLine = singleLine,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (isPassword) KeyboardType.Password else keyboardType),
        modifier = overrideModifier ?: modifier.padding(dimensionResource(R.dimen.padding_8dp))
    )
}

@Composable
inline fun AppDataList(
    entities: List<DataEntity>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    crossinline card: @Composable (DataEntity) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.padding_8dp))
    ) {
        items(items = entities, key = { it.id }) {
            card(it)
        }
    }
}

@Composable
inline fun AppDataChoosingList(
    entities: List<DataEntity>,
    chosenEntityId: Int?,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline card: @Composable (DataEntity, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.animateContentSize(
            animationSpec = spring(Spring.DampingRatioNoBouncy, Spring.StiffnessMedium)
        )
    ) {
        if (chosenEntityId != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_8dp))
            ) {
                card(entities.first { it.id == chosenEntityId }, true)
            }
        } else {
            AppDataList(
                entities = entities,
                modifier = modifier.heightIn(Dp.Unspecified, maxHeight),
                contentPadding = contentPadding,
                card = { card(it, false) }
            )
        }
    }
}

@Composable
inline fun AppDataMultiChoosingList(
    entities: List<DataEntity>,
    chosenEntityIds: Set<Int>,
    maxHeight: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    crossinline card: @Composable (DataEntity, Boolean) -> Unit
) {
    AppDataList(
        entities = entities,
        modifier = modifier.heightIn(Dp.Unspecified, maxHeight),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally,
        card = { card(it, chosenEntityIds.contains(it.id)) }
    )
}

@Composable
fun AppYesNoRadioGroup(
    isSelectedYes: Boolean?,
    onSelect: (Boolean) -> Unit,
    testTagSuffix: String = ""
) {
    AppRadioGroup(
        options = listOf(true, false),
        selectedOption = isSelectedYes,
        onSelect = onSelect,
        optionDescriptionRes = { if (it) R.string.yes else R.string.no },
        testTagSuffix = testTagSuffix
    )
}

@Composable
fun <T> AppRadioGroup(
    options: List<T>,
    selectedOption: T?,
    onSelect: (T) -> Unit,
    optionDescriptionRes: (T) -> Int,
    testTagSuffix: String = ""
) {
    FlowRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEach { option ->
            AppRadioOption(
                descriptionRes = optionDescriptionRes(option),
                isSelected = selectedOption == option,
                onSelect = { onSelect(option) },
                testTagSuffix = testTagSuffix
            )
        }
    }
}

@Composable
fun AppRadioOption(
    @StringRes descriptionRes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    testTagSuffix: String = ""
) {
    val description = stringResource(descriptionRes)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = dimensionResource(R.dimen.padding_8dp))
            .testTag(description + testTagSuffix)
    ) {
        RadioButton(
            selected = isSelected,
            onClick = null
        )
        AppMediumTitleText(
            text = description,
            overrideModifier = Modifier.padding(
                start = dimensionResource(R.dimen.padding_8dp),
                end = dimensionResource(R.dimen.padding_8dp)
            )
        )
    }
}

@Composable
inline fun AppList(
    entities: List<DataEntity>,
    content: @Composable (DataEntity) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(R.dimen.padding_8dp))
    ) {
        entities.forEachIndexed { index, entity ->
            content(entity)
            if (index != entities.lastIndex)
                HorizontalDivider()
        }
    }
}

@Composable
inline fun AppEntityCard(
    entity: DataEntity,
    crossinline onEntityClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (DataEntity) -> Unit
) {
    Card(
        onClick = { onEntityClick(entity.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = { content(entity) }
    )
}

@Composable
inline fun AppExpandedEntityCard(
    entity: DataEntity,
    modifier: Modifier = Modifier,
    crossinline content: @Composable (DataEntity, Boolean) -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Card(
        onClick = { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_4dp)),
        modifier = modifier.padding(dimensionResource(R.dimen.padding_8dp)),
        content = { content(entity, isExpanded) }
    )
}