package xelagurd.socialdating.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import xelagurd.socialdating.R
import xelagurd.socialdating.data.model.DataEntity

@Composable
@ReadOnlyComposable
fun stringResourceWithColon(@StringRes id: Int) =
    stringResource(R.string.text_with_colon, stringResource(id))

@Composable
fun AppLargeBodyText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
    )
}

@Composable
fun AppSmallTitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_very_small))
    )
}

@Composable
fun AppMediumTitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small))
    )
}

@Composable
fun AppLargeTitleText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium))
    )
}

@Composable
fun AppMediumTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_small))
    ) {
        AppMediumTitleText(text)
    }
}

@Composable
fun AppLargeTextCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    AppTextCard(
        isEnabled = isEnabled,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium))
    ) {
        AppLargeTitleText(text)
    }
}

@Composable
private fun AppTextCard(
    isEnabled: Boolean,
    onClick: () -> Unit,
    elevation: CardElevation,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        enabled = isEnabled,
        onClick = onClick,
        elevation = elevation,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        content = content
    )
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small))
    )
}

@Composable
inline fun AppDataLazyList(
    entities: List<DataEntity>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    crossinline card: @Composable (DataEntity) -> Unit
) {
    LazyColumn(
        contentPadding = contentPadding,
        modifier = modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        items(items = entities, key = { it.id }) {
            card(it)
        }
    }
}

@Composable
inline fun AppDataList(
    entities: List<DataEntity>,
    modifier: Modifier = Modifier,
    content: @Composable (DataEntity) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_small))
    ) {
        entities.forEach {
            content(it)
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
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
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
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_medium)),
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        content = { content(entity, isExpanded) }
    )
}