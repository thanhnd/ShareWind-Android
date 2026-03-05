package com.sharewind.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sharewind.app.ui.theme.ShareWindTheme

/**
 * Reusable empty state for lists (rides, requests, etc.).
 * Use when Home/Feed screens display zero results.
 *
 * Per [ui-ux-expert.mdc]: Design for zero-data and failure cases.
 */
@Composable
fun EmptyStateContent(
    message: String,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit = {
        Icon(
            imageVector = Icons.Outlined.Inbox,
            contentDescription = "Empty",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    },
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}

/**
 * Empty state for ride feed — "No rides available".
 */
@Composable
fun EmptyRidesContent(
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null
) {
    EmptyStateContent(
        message = "No rides available yet.\nCheck back later or create a request.",
        modifier = modifier,
        icon = {
            Icon(
                imageVector = Icons.Outlined.DirectionsCar,
                contentDescription = "No rides",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        },
        actionLabel = if (onRefresh != null) "Refresh" else null,
        onAction = onRefresh
    )
}

@Preview(showBackground = true)
@Composable
fun EmptyStateContentPreview() {
    ShareWindTheme {
        EmptyRidesContent()
    }
}
