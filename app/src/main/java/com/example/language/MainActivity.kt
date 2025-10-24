package com.example.language

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.language.ui.theme.Indigo500
import com.example.language.ui.theme.LanguageTheme
import com.example.language.ui.theme.Purple600
import java.io.InputStreamReader

data class VocabularyItem(val lesson: String, val native: String, val english: String, val pronunciation: String)

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Learn : Screen("learn", "Learn", Icons.Default.Create)
    object Speak : Screen("speak", "Speak", Icons.Default.Menu)
    object Progress : Screen("progress", "Progress", Icons.Default.Settings)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}

const val LESSON_DETAIL_ROUTE = "lesson_detail/{languageName}"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LanguageTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Learn, Screen.Speak, Screen.Progress, Screen.Profile)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any {
                        it.route == screen.route || (screen.route == Screen.Learn.route && it.route?.startsWith("lesson_detail") == true)
                    } == true

                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Learn.route) { LearnTabScreen(navController, innerPadding) }
            composable(
                route = LESSON_DETAIL_ROUTE,
                arguments = listOf(navArgument("languageName") { type = NavType.StringType })
            ) { backStackEntry ->
                val languageName = backStackEntry.arguments?.getString("languageName")
                if (languageName != null) {
                    LearnScreen(languageName, innerPadding)
                }
            }
            composable(Screen.Speak.route) { SpeakScreen(innerPadding) }
            composable(Screen.Progress.route) { ProgressScreen(innerPadding) }
            composable(Screen.Profile.route) { ProfileScreen(innerPadding) }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Welcome Card
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Welcome back, Skye!!!", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = "Streak", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("7 day streak", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Current language", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text("isiZulu", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Level", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text("Beginner", fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("XP", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text("1,250", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Daily goal (20 min)", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("15/20 min", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = 0.75f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Challenge
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Indigo500, Purple600)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text("Daily Challenge", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                    Text("Complete today's challenge to earn bonus XP!", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /*TODO*/ },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Start Challenge", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continue Learning
        Text("Continue Learning", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Lesson", tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Greetings & Introductions", fontWeight = FontWeight.Medium)
                        Text("Lesson 3 of 5", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Progress", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text("60%", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = 0.6f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Continue Lesson", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Practice
        Text("Quick Practice", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PracticeCard("Listening", Icons.Default.PlayArrow)
            PracticeCard("Speaking", Icons.Default.Call)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PracticeCard("Writing", Icons.Default.Edit)
            PracticeCard("Flashcards", Icons.Default.List)
        }
    }
}

@Composable
fun LanguageCard(name: String, description: String, color: Color, navController: NavController) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .width(110.dp)
            .clickable { navController.navigate("lesson_detail/$name") }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f), minLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Start Learning", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun PracticeCard(name: String, icon: ImageVector) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = name, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontWeight = FontWeight.Medium)
        }
    }
}

fun loadVocabularyFromAssets(context: Context, fileName: String): List<VocabularyItem> {
    val vocabList = mutableListOf<VocabularyItem>()
    try {
        val inputStream = context.assets.open(fileName)
        val reader = InputStreamReader(inputStream)
        reader.readLines().drop(1).forEach { line ->
            val parts = line.split(",")
            if (parts.size >= 4) {
                vocabList.add(VocabularyItem(parts[0], parts[1], parts[2], parts[3]))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return vocabList
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LearnScreen(languageName: String, innerPadding: PaddingValues) {
    val context = LocalContext.current
    val vocabulary = loadVocabularyFromAssets(context, "$languageName.csv".lowercase())
    val groupedVocabulary = vocabulary.groupBy { it.lesson }

    if (vocabulary.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No vocabulary found. Check the file in your assets folder and ensure it has a \"Lesson\" column.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(16.dp)
        ) {
            stickyHeader {
                Text(
                    languageName,
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            groupedVocabulary.forEach { (lesson, items) ->
                stickyHeader {
                    Text(
                        lesson,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                items(items) { item ->
                    VocabularyCard(item)
                }
            }
        }
    }
}

@Composable
fun VocabularyCard(item: VocabularyItem) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.native, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(item.english, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(item.pronunciation, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
        }
    }
}

@Composable
fun LearnTabScreen(navController: NavController, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        Text("Learn a Language", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            LanguageCard("isiZulu", "Most widely spoken home language.", Color.Red, navController)
            LanguageCard("Xitsonga", "Mainly spoken in Limpopo province.", Color.Green, navController)
            LanguageCard("Tshivenda", "Spoken in the northern regions.", Color.Yellow, navController)
        }
    }
}

@Composable
fun SpeakScreen(innerPadding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("Speak Screen")
    }
}

@Composable
fun ProgressScreen(innerPadding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("Progress Screen")
    }
}

@Composable
fun ProfileScreen(innerPadding: PaddingValues) {
    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
        Text("Profile Screen")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LanguageTheme {
        HomeScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LanguageTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LearnScreenPreview() {
    LanguageTheme {
        LearnScreen("Tshivenda", PaddingValues(0.dp))
    }
}
