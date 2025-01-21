package com.albsig.stundenmanager.ui.adminuserdetails;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.albsig.stundenmanager.R;
import com.albsig.stundenmanager.common.Constants;
import com.albsig.stundenmanager.common.callbacks.Result;
import com.albsig.stundenmanager.common.callbacks.ResultCallback;
import com.albsig.stundenmanager.common.viewmodel.admin.AdminViewModel;
import com.albsig.stundenmanager.databinding.FragmentAdminUserDetailsBinding;
import com.albsig.stundenmanager.domain.model.ShiftModel;
import com.albsig.stundenmanager.domain.model.UserModel;
import com.albsig.stundenmanager.ui.adminworkeradministration.AdminUserAdministrationFragment;
import com.albsig.stundenmanager.ui.vacationillness.VIAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdminUserDetailsFragment extends Fragment implements ApprovalAdapter.OnUserApprovalClickListener {

    private static final String TAG = "AdminUserDetailsFragment";
    Context mContext;
    FragmentAdminUserDetailsBinding binding;
    AdminViewModel adminViewModel;
    RecyclerView rvApproval;
    RecyclerView rvVI;
    VIAdapter viAdapter;
    ApprovalAdapter approvalAdapter;
    UserModel userModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminViewModel = new ViewModelProvider(requireActivity()).get(AdminViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUserDetailsBinding.inflate(inflater,  container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigateBack();
        initObserver();
        initRecyclerVI();
        initRecyclerApproval();
        initCreateReportBtn();
    }

    private void initCreateReportBtn() {
        binding.btnCreateReport.setOnClickListener(view -> {
            savePdfToDownloads();
        });
    }

    private void initRecyclerApproval() {
        rvApproval = binding.rvApproval;
        approvalAdapter = new ApprovalAdapter(this);
        rvApproval.setAdapter(approvalAdapter);
    }

    private void initRecyclerVI() {
        rvVI = binding.rvVI;
        viAdapter = new VIAdapter();
        rvVI.setAdapter(viAdapter);
    }


    private void initObserver() {
        adminViewModel.getUserDetailModel().observe( getViewLifecycleOwner(), userModel -> {
            if (userModel == null) {
                return;
            }

            Log.d(TAG, userModel.toString());
            binding.tvName.setText(userModel.getName());
            binding.tvSurname.setText(userModel.getSurname());
            adminViewModel.getCheckedVIList(userModel.getUid());
            adminViewModel.getVIListToCheck(userModel.getUid());
            this.userModel = userModel;
        });

        adminViewModel.getCheckedVIList().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                return;
            }

            viAdapter.updateData(result.getValue());
        });

        adminViewModel.getVIListToCheck().observe(getViewLifecycleOwner(), result -> {
            if (!result.isSuccess()) {
                return;
            }

            approvalAdapter.updateData(result.getValue());
        });
    }

    private void navigateBack() {
        FloatingActionButton fabNavBack = binding.fabBackNav;

        fabNavBack.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, AdminUserAdministrationFragment.class, null, Constants.TAG_WORKER_ADMINISTRATION).setReorderingAllowed(true);
            fragmentTransaction.commit();
        });
    }

    @Override
    public void onUserApprovalUpdate(String approvalType, String uid, String docId) {
        adminViewModel.updateVIModel(approvalType, uid, docId, new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> response) {
                Toast.makeText(mContext, "Successfully updated with approval type: " + approvalType, Toast.LENGTH_SHORT).show();
                adminViewModel.getCheckedVIList(uid);
                adminViewModel.getVIListToCheck(uid);
            }

            @Override
            public void onError(Result<Boolean> error) {
                Toast.makeText(mContext, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void savePdfToDownloads() {
        LocalDateTime localData = LocalDateTime.now();
        String todayDateStr = localData.getYear() + "_" + localData.getMonthValue() + "_" + localData.getDayOfMonth() + "-" + localData.getHour() + "_" + localData.getMinute();
        String fileName = "Report_" +userModel.getName() + "_" + userModel.getSurname() + todayDateStr + Constants.FILE_TYPE_PDF;
        adminViewModel.getContents(localData, userModel, 2025, new ResultCallback<List<String>>() {
            @Override
            public void onSuccess(Result<List<String>> content) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                    //put in Downloads folder
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");

                    Uri uri = requireActivity().getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
                    try (OutputStream outputStream = requireActivity().getContentResolver().openOutputStream(uri)) {
                        if (outputStream != null) {
                            // PDF erstellen
                            PdfDocument document = new PdfDocument();
                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();

                            // Einstellungen
                            Paint paint = new Paint();
                            paint.setTextSize(10);
                            paint.setColor(Color.BLACK);

                            float x = 10; // Startposition X
                            float y = 50; // Startposition Y
                            float lineHeight = 20; // Höhe einer Zeile
                            float pageHeight = 600; // Gesamthöhe der Seite
                            float bottomMargin = 50; // Unterer Rand
                            int linesPerPage = (int) ((pageHeight - bottomMargin) / lineHeight); // Zeilen pro Seite

                            int lineCount = 0;
                            PdfDocument.Page page = document.startPage(pageInfo);

                            for (String line : content.getValue()) {
                                // Neue Seite starten, wenn der Platz nicht mehr ausreicht
                                if (lineCount > 0 && lineCount % linesPerPage == 0) {
                                    document.finishPage(page);
                                    pageInfo = new PdfDocument.PageInfo.Builder(300, 600, (lineCount / linesPerPage) + 1).create();
                                    page = document.startPage(pageInfo);
                                    y = 50; // Zurück zur Startposition
                                }

                                // Text auf der aktuellen Seite zeichnen
                                page.getCanvas().drawText(line, x, y, paint);
                                y += lineHeight;
                                lineCount++;
                            }

                            // Letzte Seite beenden
                            document.finishPage(page);

                            // PDF schreiben und schließen
                            document.writeTo(outputStream);
                            document.close();

                            Toast.makeText(mContext, "PDF gespeichert in Downloads", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Fehler beim Speichern der PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(Result<List<String>> error) {

            }
        });
    }

    private void addTitle(PdfDocument.Page page) {
        Paint paint = new Paint();
        paint.setTextSize(10);
        paint.setColor(Color.BLACK);
        page.getCanvas().drawText(Constants.APP_NAME, 12, 12, paint);
    }
}
