package xelagurd.socialdating.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import xelagurd.socialdating.AppBottomNavigationBar
import xelagurd.socialdating.AppTopBar
import xelagurd.socialdating.data.fake.FakeDataSource
import xelagurd.socialdating.data.model.Category
import xelagurd.socialdating.ui.navigation.CategoriesDestination
import xelagurd.socialdating.ui.state.CategoriesUiState
import xelagurd.socialdating.ui.theme.AppTheme
import xelagurd.socialdating.ui.viewmodel.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onCategoryClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    categoriesViewModel: CategoriesViewModel = hiltViewModel()
) {
    val categoriesUiState by categoriesViewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(CategoriesDestination.titleRes),
                internetStatus = categoriesUiState.internetStatus,
                refreshAction = categoriesViewModel::getCategories,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            AppBottomNavigationBar(
                currentTopLevelRoute = CategoriesDestination.topLevelRoute
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        DataListComponent(
            dataListUiState = categoriesUiState,
            contentPadding = innerPadding
        ) {
            AppEntityCard(
                entity = it,
                onEntityClick = onCategoryClick
            ) {
                CategoryCardContent(
                    category = it as Category
                )
            }
        }
    }
}

@Composable
fun CategoryCardContent(
    category: Category,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        AppLargeTitleText(text = category.name)
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoriesComponentPreview() {
    AppTheme {
        DataListComponent(
            dataListUiState = CategoriesUiState(entities = FakeDataSource.categories)
        ) {
            AppEntityCard(
                entity = it,
                onEntityClick = { }
            ) {
                CategoryCardContent(
                    category = it as Category
                )
            }
        }
    }
}
