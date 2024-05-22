

package com.example.szlaki

import TrailData1
import TrailData2
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.SoundEffectConstants
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import com.example.szlaki.ui.theme.SzlakiTheme
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import java.util.Objects


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var stopwatch = Stopwatch()
        setContent {
            SzlakiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainView()
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView() {
    var stopwatch = remember {
        Stopwatch()
    }

    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState(pageCount = {3})
    var state1 = false;

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Trails") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    StopwatchPanel(stopwatch)
                }
            )

        },
        content = {

            HorizontalPager(
                state = pageState,
                modifier = Modifier.padding(it)
            ) { page ->
                when (page) {
                    0 -> listDetailPaneScaffoldImp(TrailData1.trails)
                    1 -> HomePage()
                    2 -> listDetailPaneScaffoldImp(TrailData2.trails)
                }
            }
        },
        bottomBar = {
            //TO DO: CHANGE TO NOT ENOUGH SCREEN HEIGHT AND ADD STOP WATCH TO TOP BAR
//            if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                BottomAppBar(
                    actions = {
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationBarItem(
                            label = {Text("Simple")},
                            icon = {Icon(Icons.Default.TrendingFlat, contentDescription = "simple")},
                            selected = false,
                            onClick = {
                                scope.launch {
                                    pageState.animateScrollToPage(0)
                                }
                                state1 = false;
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors())
                        Spacer(modifier = Modifier.weight(1.2f))
                        NavigationBarItem(
                            label = {Text("Home")},
                            icon = {Icon(Icons.Filled.Home, contentDescription = "home")},
                            selected = state1,
                            onClick = {
                                scope.launch {
                                    pageState.animateScrollToPage(1)
                                }
                                state1 = true;
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors())
                        Spacer(modifier = Modifier.weight(1.2f))
                        NavigationBarItem(
                            label = {Text("Hard")},
                            icon = {Icon(Icons.Default.TrendingUp, contentDescription = "hard")},
                            selected = false,
                            onClick = {
                                scope.launch {
                                    pageState.animateScrollToPage(2)
                                }
                                state1 = false;
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors())
                        Spacer(modifier = Modifier.weight(1f))
                    }
                )
        }
    )
}

@Composable
fun HomePage() {
    Column {
        Text("Hello")
    }
}


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun listDetailPaneScaffoldImp(trailList: List<Trail>) {
    var selectedItem: SelectedTrail? by rememberSaveable(stateSaver = SelectedTrail.Saver) {
        mutableStateOf(SelectedTrail(0))
    }

    // Create the ListDetailPaneScaffoldState
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        modifier = Modifier.padding(0.dp),
        scaffoldState = navigator.scaffoldState,
        listPane = {
            AnimatedPane(Modifier) {
                List(
                    onItemClick = { id ->
                        selectedItem = id
                        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail)
                    },
                    trailList,
                )
            }
        },
        detailPane = {
            AnimatedPane(Modifier) {
                // Show the detail pane content if selected item is available
                selectedItem?.let { item ->
                    Details(item,trailList)
                }
            }
        },
    )
}

@Composable
fun List(onItemClick: (SelectedTrail) -> Unit , trailList: List<Trail>) {

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        trailList.forEachIndexed { id, trail ->
            item {
                ElevatedCard(modifier = Modifier.padding(5.dp)) {
                    Column(modifier = Modifier.clickable {
                        onItemClick(SelectedTrail(id))
                    }) {
                        Image(
                            painter = painterResource(R.drawable.trail),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(0.dp, 150.dp),
                            )

                    }
                    ListItem(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(SelectedTrail(id))
                            },
                        headlineContent = {
                            Text(text = trail.name, style = MaterialTheme.typography.titleMedium)
                        },
                    )
                }

            }

        }
    }

}

//private fun startCamera(
//    context: Context,
//    lifecycleOwner: LifecycleOwner,
//    previewView: PreviewView,
//    onScan: (String) -> Unit
//) {
//    val cameraController = LifecycleCameraController(context)
//    val options = BarcodeScannerOptions.Builder()
//        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
//        .build()
//
//    val barcodeScanner = BarcodeScanning.getClient(options)
//    val mlKitAnalyzer = MlKitAnalyzer(
//        listOf(barcodeScanner),
//        COORDINATE_SYSTEM_VIEW_REFERENCED,
//        ContextCompat.getMainExecutor(context)
//    ) { result ->
//        val barcodeResult = result?.getValue(barcodeScanner)
//        val scannedBarcodeValue = barcodeResult?.firstOrNull()?.rawValue ?: ""
//        onScan(scannedBarcodeValue)
//    }
//    cameraController.setImageAnalysisAnalyzer(
//        ContextCompat.getMainExecutor(context),
//        mlKitAnalyzer
//    )
//    cameraController.bindToLifecycle(lifecycleOwner)
//    previewView.controller = cameraController
//}

@Composable
fun Details(item: SelectedTrail , trailList: List<Trail>) {
//    val context = LocalContext.current
//    val cameraController: LifecycleCameraController =
//        remember { LifecycleCameraController(context) }
//    val previewView: PreviewView = remember { PreviewView(context) }
//    val lifecycleOwner = LocalLifecycleOwner.current



    val text = trailList[item.id].name
    Card {

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Image(
                painter = painterResource(R.drawable.trail),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(topEnd = 12.dp, topStart = 12.dp))
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.size(4.dp))
            Text(
                text = "Details page for $text",
                fontSize = 24.sp,
            )
            Spacer(Modifier.size(16.dp))
            Text(
                text = "TODO: Add details"
            )
            Spacer(modifier = Modifier.weight(1f))
            Row {
                Spacer(modifier = Modifier.weight(1f))
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Filled.SaveAlt, contentDescription = "Localized description")
                }
                Spacer(modifier = Modifier.size(6.dp))
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "Localized description")
                }
            }


        }
    }
}



class SelectedTrail(val id: Int) {
    companion object {
        val Saver: Saver<SelectedTrail?, Int> = Saver(
            { it?.id },
            ::SelectedTrail,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    var stopwatch = Stopwatch()
    SzlakiTheme {
        Surface {
            MainView()
        }
    }
}