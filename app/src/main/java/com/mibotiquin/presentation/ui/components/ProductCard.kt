package com.mibotiquin.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mibotiquin.R
import com.mibotiquin.domain.model.Category
import com.mibotiquin.domain.model.ExpiryStatus
import com.mibotiquin.domain.model.ProductUiModel
import com.mibotiquin.presentation.ui.theme.ExpiryColors

@Composable
fun ProductCard(
    product: ProductUiModel,
    onQuantityChange: (Int) -> Unit,
    onExpiryDateChange: (Long) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val expiryColors = ExpiryColors(product.expiryStatus)
    val hasUrgency = product.expiryStatus != ExpiryStatus.OK

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.surface,
        tonalElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = if (hasUrgency) 1.5.dp else 1.dp,
            color = if (hasUrgency) expiryColors.border else colors.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icono de categoría
            Icon(
                imageVector = categoryIcon(product.product.category),
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(28.dp)
            )

            // Nombre + metadata
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = product.product.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colors.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuantityStepper(
                        quantity = product.product.quantity,
                        onChange = onQuantityChange
                    )
                    ExpiryChip(
                        status = product.expiryStatus,
                        daysText = product.formattedDaysUntilExpiry,
                        expiryDate = product.formattedExpiryDate,
                        colors = expiryColors
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.DeleteOutline,
                    contentDescription = "Eliminar",
                    tint = colors.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onChange: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val enabled = quantity > 0

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(
            onClick = { onChange(quantity - 1) },
            enabled = enabled,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Remove,
                contentDescription = "Disminuir cantidad",
                tint = if (enabled) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.38f)
            )
        }
        Text(
            text = quantity.toString().padStart(2, '0'),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = if (enabled) colors.onSurface else colors.onSurfaceVariant.copy(alpha = 0.6f)
        )
        IconButton(
            onClick = { onChange(quantity + 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Aumentar cantidad",
                tint = colors.primary
            )
        }
    }
}

@Composable
private fun ExpiryChip(
    status: ExpiryStatus,
    daysText: String,
    expiryDate: String,
    colors: com.mibotiquin.presentation.ui.theme.ExpiryColorSet
) {
    val themeColors = MaterialTheme.colorScheme

    if (status == ExpiryStatus.OK) {
        Text(
            text = "Caduca $expiryDate",
            style = MaterialTheme.typography.bodyMedium,
            color = themeColors.onSurfaceVariant
        )
    } else {
        Row(
            modifier = Modifier
                .background(colors.background, RoundedCornerShape(100.dp))
                .border(1.5.dp, colors.border, RoundedCornerShape(100.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = when (status) {
                    ExpiryStatus.SOON -> Icons.Filled.Schedule
                    ExpiryStatus.CRITICAL -> Icons.Filled.WarningAmber
                    ExpiryStatus.EXPIRED -> Icons.Filled.ErrorOutline
                    ExpiryStatus.EMPTY -> Icons.Filled.Inventory2
                    else -> Icons.Filled.Schedule
                },
                contentDescription = null,
                tint = colors.icon,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = when (status) {
                    ExpiryStatus.SOON, ExpiryStatus.CRITICAL -> daysText
                    ExpiryStatus.EXPIRED -> stringResource(R.string.product_expired)
                    ExpiryStatus.EMPTY -> stringResource(R.string.product_empty)
                    else -> ""
                },
                style = MaterialTheme.typography.labelMedium,
                color = colors.text
            )
        }
    }
}

private fun categoryIcon(category: Category): ImageVector = when (category) {
    Category.MEDICINE -> Icons.Filled.Medication
    Category.FIRST_AID -> Icons.Filled.LocalHospital
    Category.TOPICAL -> Icons.Filled.Healing
}
