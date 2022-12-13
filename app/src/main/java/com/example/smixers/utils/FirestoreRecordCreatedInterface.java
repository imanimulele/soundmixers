package com.example.smixers.utils;

import java.util.Map;


public interface FirestoreRecordCreatedInterface
{
    void onFirestoreRecordCreated(String collName, final String docId, Object docObject);
    void onFirestoreRecordCreated(String collName, final String docId,  Map<String, Object> fields);
}
