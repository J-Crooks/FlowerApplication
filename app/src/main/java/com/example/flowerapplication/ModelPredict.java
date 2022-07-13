package com.example.flowerapplication;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flowerapplication.ml.FlowerModel;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ModelPredict extends Fragment {
    private static final String ARG_ENCODEDIMG = "";
    private Bitmap flower;

    private ImageView previewImage;
    private Button restart;
    private Button addFlower;
    private TextView labeltop;
    private TextView probtop;

    private HelperFunctions helper;

    public ModelPredict() {
        // Required empty public constructor
    }

    public static ModelPredict newInstance(String codedImage) {
        ModelPredict fragment = new ModelPredict();
        Bundle args = new Bundle();
        args.putString(ARG_ENCODEDIMG, codedImage);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new HelperFunctions();
        if (getArguments() != null) {
            flower = helper.ConvertEncodedStringtoBitmap(getArguments().getString(ARG_ENCODEDIMG));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_model_predict, container, false);
        previewImage = v.findViewById(R.id.mpreviewFlower);
        previewImage.setImageBitmap(flower);
        restart = v.findViewById(R.id.backToStart);
        addFlower  = v.findViewById(R.id.addFlower);
        labeltop = v.findViewById(R.id.labeltop1);
        probtop = v.findViewById(R.id.probtop1);

        Map<String, Float> outputs = ImageAnalysis();
        setProbLabelTxt(outputs);

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoRetrieval n = PhotoRetrieval.newInstance();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();
            }
        });

        addFlower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FlowerCollectionDB.FeedReaderDbHelper myFlowersHelper =  new FlowerCollectionDB.FeedReaderDbHelper(getContext());
                SQLiteDatabase db = myFlowersHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(FlowerCollectionDB.FeedEntry.COL_FLOWER, outputs.entrySet().iterator().next().getKey());
                values.put(FlowerCollectionDB.FeedEntry.COL_IMAGE, getArguments().getString(ARG_ENCODEDIMG));

                db.insert(FlowerCollectionDB.FeedEntry.TABLE_NAME, null, values); //INSERT ROW INTO DATABASE

                myFlowersHelper.close();
                db.close();

                FlowerCollection n = FlowerCollection.newInstance();
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction().replace(R.id.fragment, n, n.getTag()).commit();

            }
        });

        return v;
    }

    public void setProbLabelTxt(Map<String, Float> outputs) {
        if (outputs != null) {
            Iterator Iter = outputs.keySet().iterator();
            DecimalFormat df = new DecimalFormat("##.#");
            String top3Label = "";
            String top3Value = "";
            for (int i = 0;  i < 5; i++) {
                String Key = (String)Iter.next();
                Float Value = outputs.get(Key);
                top3Label = top3Label.concat(Key + "\n");
                top3Value = top3Value.concat(df.format(Value) + "%\n");
            }
            labeltop.setText(top3Label);
            probtop.setText(top3Value);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Map ImageAnalysis() {
        try {
            FlowerModel flowerModel = FlowerModel.newInstance(this.getContext()); //MODEL
            TensorBuffer inputFeature = TensorBuffer.createFixedSize(new int[]{1, 100, 100, 3}, DataType.FLOAT32); //BUFFER CONTAINER
            ImageProcessor imageProcessor = //IMAGE PRE-PROCESSING FOR IMAGE
                new ImageProcessor.Builder()
                    .add(new ResizeOp(100,100, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0,255))
                    .build();

            //CONVERTS BITMAP TO TFIMAGE FOR PROCESSING
            TensorImage tfImage = new TensorImage(DataType.FLOAT32);
            tfImage.load(flower);
            tfImage = imageProcessor.process(tfImage);

            //RUNS INFERENCE OF IMAGE
            inputFeature.loadBuffer(tfImage.getBuffer());
            FlowerModel.Outputs outputs = flowerModel.process(inputFeature);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            //MAPS OUTPUTS AND LABELS TOGETHER
            List<String> labels = FileUtil.loadLabels(this.getContext(), "labels"); //LOADS LABELS FROM FILE
            float[] probs = outputFeature0.getFloatArray(); //CONVERTS BUFFER TO A FLOAT ARRAY
            Map<String, Float> probsWithLabels = new HashMap<String, Float>(); //INIT MAP
            for (int i = 0; i < labels.size(); i++) {
                probsWithLabels.put(labels.get(i), probs[i]*100); //ADDS LABEL WITH VALUE INTO MAP FOR X NUM OF LABELS
            }

            //SORTS MAPS BY VALUE IN ASCENDING ORDER AND REVERSED
            LinkedHashMap<String, Float> sortedMap = new LinkedHashMap<>();
            probsWithLabels.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

            flowerModel.close();
            return sortedMap;

        } catch (IOException e) {
            return null;
        }
    }
}



