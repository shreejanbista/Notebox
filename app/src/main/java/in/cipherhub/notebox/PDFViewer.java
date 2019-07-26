package in.cipherhub.notebox;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.cipherhub.notebox.utils.PdfRenderer;

public class PDFViewer extends AppCompatActivity {

  public static final String FRAGMENT_PDF_RENDERER = "pdf_renderer";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pdfviewer);

    String s = getIntent().getStringExtra("file_name");

    Log.i("FILENAME", s);

    if (savedInstanceState == null) {

      Bundle bundle = new Bundle();
      bundle.putString("file_name", s);

      PdfRenderer pdfRenderer = new PdfRenderer();
      pdfRenderer.setArguments(bundle);

      getSupportFragmentManager().beginTransaction()
              .replace(R.id.container, pdfRenderer,
                      FRAGMENT_PDF_RENDERER).commit();

    }


  }
}
