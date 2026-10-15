package com.airtribe.ridewise.util;

import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Identifiable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore<T extends Identifiable> {
    private final Map<String, T> records = new HashMap<>();

    public boolean saveIfAbsent(T record) {
        if (record == null) {
            throw new InvalidDataException("Record cannot be null");
        }

        if (records.containsKey(record.getId())) {
            return false;
        }

        records.put(record.getId(), record);
        return true;
    }

    public T findById(String id) {
        return records.get(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(records.values());
    }
}
