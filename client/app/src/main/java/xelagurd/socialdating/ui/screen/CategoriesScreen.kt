package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.R
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesViewModel = viewModel()
) {
    val categoriesUiState by categoriesViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(CategoriesDestination.titleRes),
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        CategoriesBody(
            categories = categoriesUiState.categories,
            onCategoryClick = onCategoryClick,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun CategoriesBody(
    categories: List<Category>,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (categories.isNotEmpty()) {
            CategoriesList(
                categories = categories,
                onCategoryClick = onCategoryClick,
                contentPadding = contentPadding,
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_small))
            )
        } else {
            Text(
                text = stringResource(R.string.no_internet_connection),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }
}

@Composable
private fun CategoriesList(
    categories: List<Category>,
    onCategoryClick: (Int) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        items(items = categories, key = { it.id }) {
            CategoryCard(
                category = it,
                onCategoryClick = onCategoryClick,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onCategoryClick(category.id) },
        elevation = CardDefaults.cardElevation(dimensionResource(R.dimen.elevation_small)),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesBodyPreview() {
    AppTheme {
        CategoriesBody(
            FakeDataSource.categories,
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriesBodyEmptyListPreview() {
    AppTheme {
        CategoriesBody(
            listOf(),
            onCategoryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryCardPreview() {
    AppTheme {
        CategoryCard(
            FakeDataSource.categories[0],
            onCategoryClick = {}
        )
    }
}
