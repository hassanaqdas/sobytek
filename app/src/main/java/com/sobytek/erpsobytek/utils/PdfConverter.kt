package com.sobytek.erpsobytek.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.print.PrintAttributes
import android.view.View
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.ref.WeakReference

interface PdfConversionCallback {
    fun onPdfConversionComplete(filePath: String)
    fun onPdfConversionFailed(error: String)
}

class PdfConverter private constructor(private val context: Context) {

    private val contextRef: WeakReference<Context> = WeakReference(context)

    companion object {
        private var instance: PdfConverter? = null

        @JvmStatic
        fun getInstance(context: Context): PdfConverter {
            return instance ?: synchronized(this) {
                instance ?: PdfConverter(context.applicationContext).also { instance = it }
            }
        }
    }

    fun convertXmlToPdf(context: Context,lotId:String,opNo:String,view: View, callback: PdfConversionCallback) {
        val context = contextRef.get()

        if (context == null) {
            callback.onPdfConversionFailed("Context is null")
            return
        }
//        try {
            // Set the view dimensions based on the default page size
//            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//            setViewDimensions(view, PrintAttributes.MediaSize.ISO_A4)
//            val document = Document()
//            document.setPageSize(PageSize.A4)
//            val filePath = getOutputFilePath(context,lotId,opNo)
//            val fileOutputStream = FileOutputStream(filePath)
//            val writer = PdfWriter.getInstance(document, fileOutputStream)
//            document.open()
////
//            val bitmap = viewToBitmap(view)
//            val image = Image.getInstance(bitmapToByteArray(bitmap))
//            document.pageSize = Rectangle(image.width, image.height)
//            document.add(image)
//
//            document.close()
//            fileOutputStream.close()
            // Create a page
//            val pdfDocument = PdfDocument()
//            val pageInfo = PdfDocument.PageInfo.Builder(600, 900, 1).create()
//            val page = pdfDocument.startPage(pageInfo)
//            val canvas: Canvas = page.canvas

            // Draw the image on the canvas
//            canvas.drawBitmap(bitmap, 0f, 0f, Paint())

            // Finish the page
//            pdfDocument.finishPage(page)

            // Save the document to a file
//            val fileName = "${lotId}_${opNo}_${System.currentTimeMillis()}.pdf"
//            val path = savePdf(pdfDocument, fileName)

            // Close the document
//            pdfDocument.close()

//            callback.onPdfConversionComplete(filePath)
//        } catch (e: Exception) {
//            callback.onPdfConversionFailed(e.message ?: "Unknown error")
//        }
//        val context = contextRef.get()
//
//        if (context == null) {
//            callback.onPdfConversionFailed("Context is null")
//            return
//        }

//        try {
//            // Set the view dimensions based on the default page size
//            setViewDimensions(view, PrintAttributes.MediaSize.ISO_A4)
//
//            val document = Document()
//            val filePath = getOutputFilePath(context,lotId,opNo)
//
//            val fileOutputStream = FileOutputStream(filePath)
//            val writer = PdfWriter.getInstance(document, fileOutputStream)
//            document.open()
//
//            val bitmap = viewToBitmap(view)
//            val image = Image.getInstance(bitmapToByteArray(bitmap))
//            document.pageSize = Rectangle(image.width, image.height)
//            document.add(image)
//
//            document.close()
//            fileOutputStream.close()
//
//            // Log the file path for debugging
//            Log.d("PdfConverter", "PDF saved at: $filePath")
//
//            callback.onPdfConversionComplete(filePath)
//
//            // Show print preview
//            showPrintPreview(context, filePath)
//        } catch (e: Exception) {
//            callback.onPdfConversionFailed(e.message ?: "Unknown error")
//            Log.e("PdfConverter", "PDF conversion failed: ${e.message}")
//        }
        try {
            // Set the view dimensions based on the default page size
            //setViewDimensions(view, PrintAttributes.MediaSize.ISO_A4)

            val document = Document(PageSize.A4, 0f, 0f, 0f, 0f)
            val filePath = getOutputFilePath(context,lotId,opNo)

            val fileOutputStream = FileOutputStream(filePath)
            val writer = PdfWriter.getInstance(document, fileOutputStream)
            document.open()

            val bitmap = viewToBitmap(view)
            val image = Image.getInstance(bitmapToByteArray(bitmap))
            // Adjust image size and alignment
            val maxWidth = PageSize.A4.width - document.leftMargin() - document.rightMargin()
            val maxHeight = PageSize.A4.height - document.topMargin() - document.bottomMargin()

            // Resize the image to fit within the page
            val imgWidth = image.width
            val imgHeight = image.height
            val widthScaleRatio = maxWidth / imgWidth

            image.scaleAbsolute(maxWidth, imgHeight * widthScaleRatio)


            // Set image alignment
            image.alignment = Image.ALIGN_CENTER // or Image.LEFT, Image.RIGHT, etc.
            document.add(image)

            document.close()
            fileOutputStream.close()

            callback.onPdfConversionComplete(filePath)

            // Send the document to the printer
//            showPrintPreview(context, filePath)
        } catch (e: Exception) {
            callback.onPdfConversionFailed(e.message ?: "Unknown error")
        }
    }

    private fun savePdf(pdfDocument: PdfDocument, fileName: String): String {
        val directoryPath = context.cacheDir.absolutePath

        val file = File(directoryPath, fileName)

        try {
            val fileOutputStream = FileOutputStream(file)
            pdfDocument.writeTo(fileOutputStream)
            fileOutputStream.close()
            return file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return "" // Return an empty string in case of an error
        }
    }

private fun getOutputFilePath(context: Context,lotId: String,opNo:String): String {
    val directoryPath = context.cacheDir.absolutePath
    val fileName = "${lotId}_${opNo}_${System.currentTimeMillis()}.pdf"
    return "$directoryPath/$fileName"
}

private fun viewToBitmap(view: View): Bitmap {
    if (view.width <= 0 || view.height <= 0) {
        // Handle the case where the view dimensions are invalid
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    }

    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

private fun setViewDimensions(view: View, defaultPageSize: PrintAttributes.MediaSize) {
    val defaultPageWidth = defaultPageSize.widthMils / 1000.0
    val defaultPageHeight = defaultPageSize.heightMils / 1000.0

    if (view.width < 0) {
        // Set the view's width and height based on the default page size
        val layoutParams = view.layoutParams
        layoutParams.width = (defaultPageWidth * context.resources.displayMetrics.density).toInt()
        layoutParams.height = (defaultPageHeight * context.resources.displayMetrics.density).toInt()
        view.layoutParams = layoutParams
    }
}

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }
}