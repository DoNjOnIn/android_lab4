package com.lab4.ui.screens.subjectDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lab4.data.db.DatabaseStorage
import com.lab4.data.entity.SubjectEntity
import com.lab4.data.entity.SubjectLabEntity
import kotlinx.coroutines.launch




@Composable
fun SubjectDetailsScreen(id: Int) {
    val context = LocalContext.current
    val db = DatabaseStorage.getDatabase(context)

    val subjectState = remember { mutableStateOf<SubjectEntity?>(null) }
    val subjectLabsState = remember { mutableStateOf<List<SubjectLabEntity>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        subjectState.value = db.subjectsDao.getSubjectById(id)
        subjectLabsState.value = db.subjectLabsDao.getSubjectLabsBySubjectId(id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "${subjectState.value?.title}",
            fontSize = 28.sp,
            color = Color(0xFFF1F1F1)
        )

        Text(
            text = "Labs",
            fontSize = 28.sp,
            color = Color(0xFFF1F1F1),
            modifier = Modifier.padding(top = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            items(subjectLabsState.value) { lab ->
                LabItem(
                    lab = lab,
                    onStatusChange = { updatedLab ->
                        coroutineScope.launch {
                            db.subjectLabsDao.updateSubjectLab(updatedLab)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun LabItem(lab: SubjectLabEntity, onStatusChange: (SubjectLabEntity) -> Unit) {
    var isInProgress by remember { mutableStateOf(lab.inProgress) }
    var isCompleted by remember { mutableStateOf(lab.isCompleted) }
    var comment by remember { mutableStateOf(lab.comment) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isInProgress, isCompleted) {
        onStatusChange(
            lab.copy(
                comment = comment,
                inProgress = isInProgress,
                isCompleted = isCompleted
            )
        )
    }

    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "${lab.title}",
                fontSize = 20.sp,
                color = Color(0xFFF1F1F1)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text =  "${lab.description}",
                fontSize = 14.sp,
                color = Color(0xFFF1F1F1)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isChecked ->
                            isCompleted = isChecked
                            if (isChecked) isInProgress = false
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            checkedColor = Color(0xFF1976D2),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = "Completed",
                        color = Color(0xFFD1D1D1),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isInProgress,
                        onCheckedChange = { isChecked ->
                            isInProgress = isChecked
                            if (isChecked) isCompleted = false
                        },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.White,
                            checkedColor = Color(0xFF1976D2),
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = "In Progress",
                        color = Color(0xFFD1D1D1),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            TextField(
                value = comment.toString(),
                onValueChange = { newComment ->
                    comment = newComment
                    coroutineScope.launch {
                        onStatusChange(lab.copy(comment = newComment))
                    }
                },
                label = { Text("Add Comment") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}
