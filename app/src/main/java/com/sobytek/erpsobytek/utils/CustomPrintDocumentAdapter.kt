package com.sobytek.erpsobytek.utils

import android.R.attr.path
import android.content.Context
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class CustomPrintDocumentAdapter(private val context: Context,private val pathName: String) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback,
        bundle: Bundle
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        } else {
            val builder = PrintDocumentInfo.Builder("file name")
            builder.setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                .build()

            callback.onLayoutFinished(builder.build(), newAttributes == oldAttributes)
        }
    }

    override fun onWrite(
        pageRanges: Array<out PageRange>,
        parcelFileDescriptor: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal?,
        writeResultCallback: WriteResultCallback
    ) {
        var inputStream: InputStream? = null
        var out: OutputStream? = null
        try {
            val file = File(pathName)
            inputStream = FileInputStream(file)
            out = FileOutputStream(parcelFileDescriptor.fileDescriptor)
            val buff = ByteArray(16384)
            var size: Int
            while (inputStream.read(buff).also { size = it } >= 0 && !cancellationSignal!!.isCanceled) {
                out.write(buff, 0, size)
            }
            if (cancellationSignal!!.isCanceled) writeResultCallback.onWriteCancelled() else {
                writeResultCallback.onWriteFinished(arrayOf<PageRange>(PageRange.ALL_PAGES))
            }
        } catch (e: Exception) {
            writeResultCallback.onWriteFailed(e.message)
            //Log.e("Harshita", e.message)
            e.printStackTrace()
        } finally {
            try {
                inputStream!!.close()
                out!!.close()
            } catch (ex: IOException) {
                Log.e("Harshita", "" + ex.message)
            }
        }
    }
}