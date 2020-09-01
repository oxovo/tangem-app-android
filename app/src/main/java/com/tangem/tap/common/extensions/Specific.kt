package com.tangem.tap.common.extensions

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.tangem.blockchain.common.Blockchain
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


fun String.toQrCode(): Bitmap {
    val hintMap = Hashtable<EncodeHintType, Any>()
    hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M // H = 30% damage
    hintMap[EncodeHintType.MARGIN] = 2

    val qrCodeWriter = QRCodeWriter()

    val size = 256

    val bitMatrix = qrCodeWriter.encode(this, BarcodeFormat.QR_CODE, size, size, hintMap)
    val width = bitMatrix.width
    val bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until width) {
            bmp.setPixel(y, x, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}

fun BigDecimal.toFormattedString(blockchain: Blockchain): String {
    val symbols = DecimalFormatSymbols(Locale.US)
    symbols.decimalSeparator = '.'
    val df = DecimalFormat()
    df.decimalFormatSymbols = symbols
    df.maximumFractionDigits = blockchain.decimals()
    df.minimumFractionDigits = 0
    df.isGroupingUsed = false
    val bd = BigDecimal(unscaledValue(), scale())
    bd.setScale(blockchain.decimals(), BigDecimal.ROUND_DOWN)
    return df.format(bd)
}

fun BigDecimal.toFiatString(rateValue: BigDecimal): String? {
    var fiatValue = rateValue.multiply(this)
    fiatValue = fiatValue.setScale(2, RoundingMode.DOWN)
    return "≈ USD  $fiatValue"
}
