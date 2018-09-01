package com.example.lakshaysharma.todo;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ListView list;
    Button button;

    private static ArrayList<String> TODO;
    private static ArrayAdapter<String> adapter;
    private static String item;
    private final String FILE = "list.txt";
    private TextToSpeech textToSpeech;
    private static boolean textSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TODO = new ArrayList<>();
        list = findViewById(R.id.list);
        button = findViewById(R.id.addItem);

        setList();
        deleteItem();
        speech();

        textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textSpeech = true;
            }
        });

    }

    private void setList(){

        adapter = new ArrayAdapter<>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                TODO);
        list.setAdapter(adapter);

    }

    public void addItem(View view) {

        button = (Button) view;
        final EditText editText = findViewById(R.id.item);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = editText.getText().toString();
                if (!item.isEmpty()){
                    TODO.add(item);
                    editText.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void deleteItem(){

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                TODO.remove(item);
                Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    private void speech(){

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (textSpeech){
                    textToSpeech.speak(TODO.get(i),
                            TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        try {
            PrintStream printStream = new PrintStream(openFileOutput(FILE, MODE_PRIVATE));
            for (String item: TODO){
                printStream.println(item);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Scanner scanner = new Scanner(openFileInput(FILE));
            while (scanner.hasNextLine()){
                TODO.add(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
