package com.lab4.ui.screens.subjectsList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.SubjectEntity
import com.lab4.ui.theme.Lab4Theme

@Composable
fun SubjectsListScreen(
    onDetailsScreen: (Int) -> Unit,
) {
    // Context - object which contains info about your app, has access to storage
    // is used for Room DB initialization
    val context = LocalContext.current
    // Getting the DB instance for screen
    val db = DatabaseStorage.getDatabase(context)
    val subjectsListState = remember { mutableStateOf<List<SubjectEntity>>(emptyList()) }

    /** ! Important !
        LaunchEffect(){...} - is the side effect which allows make operations in another thread (for accessing to DB)
     *  This example LaunchEffect(Unit) - means that the operations inside will be performed only once on creating the screen
     */
    LaunchedEffect(Unit) {
        // fetching the Subject from DB (subjectsDao is used)
        subjectsListState.value = db.subjectsDao.getAllSubjects()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(8.dp)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(subjectsListState.value) { subject ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp))
                        .clickable { onDetailsScreen(subject.id) }
                        .padding(16.dp)
                ) {
                    Text(
                        text = subject.title,
                        fontSize = 24.sp,
                        color = Color(0xFFF1F1F1), // Light text for contrast
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

