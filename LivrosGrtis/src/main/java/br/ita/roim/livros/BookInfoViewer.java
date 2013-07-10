package br.ita.roim.livros;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import br.ita.roim.livros.database.Book;
import br.ita.roim.livros.database.DownloadedBooksDatabase;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookInfoViewer extends FragmentActivity {

    private Book mBook;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private boolean downloaded = false;
    private boolean isDownloading = false;

    private Button read_book;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info_viewer);

        Intent usedIntent = getIntent();
        mBook = (Book) usedIntent.getSerializableExtra("book");

        TextView book_title = (TextView) findViewById(R.id.book_title);
        TextView book_author = (TextView) findViewById(R.id.book_author);

        book_title.setText(mBook.getName());
        book_author.setText(mBook.getAuthor());

        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);

        if (DownloadedBooksDatabase.getBook(mBook.getID()) != null) downloaded = true;
        if (downloaded) pb.setProgress(100);

        read_book = (Button) findViewById(R.id.read_book);
        if (!downloaded) read_book.setVisibility(View.INVISIBLE);
    }

    public void onClickDownload(View v) {
        if (downloaded) {
            Toast.makeText(this, "Livro já baixado!", 1000).show();
            return;
        }

        if (isDownloading) {
            Toast.makeText(this, "Download em andamento!", 1000).show();
            return;
        }

        final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setEnabled(true);

        Callable<Void> downloader = new Callable<Void>() {
            @Override
            public Void call() throws Exception {
            try {
                isDownloading = true;
                String path = "http://www.gutenberg.org/ebooks/" + Integer.toString(mBook.getID()) + ".epub.noimages";

                String FILENAME = "pg" + Integer.toString(mBook.getID()) + ".epub";

                URL u = new URL(path);
                HttpURLConnection c = (HttpURLConnection) u.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                FileOutputStream f = openFileOutput(FILENAME, Context.MODE_PRIVATE);

                InputStream in = c.getInputStream();

                long total = c.getContentLength();
                long current = 0;

                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ( (len1 = in.read(buffer)) > 0 ) {
                    f.write(buffer, 0, len1);
                    current += len1;
                    pb.setProgress((int) (100*current/total));
                }

                f.close();

                DownloadedBooksDatabase.addBook(mBook);

                downloaded = true;
                read_book.setVisibility(View.VISIBLE);

            } catch (MalformedURLException e) {
                Log.e("net", e.getMessage());
            } catch (ProtocolException e) {
                Log.e("net", e.getMessage());
            } catch (FileNotFoundException e) {
                Log.e("net", e.getMessage());
            } catch (IOException e) {
                Log.e("net", e.getMessage());
            }
            finally {
                isDownloading = false;
                return null;
            }
            }
        };

        executor.submit(downloader);
    }

    public void onReadBook(View view) {
        Intent myIntent = new Intent(this, BookReader.class);
        myIntent.putExtra("book", mBook.getID());
        startActivity(myIntent);
    }
}