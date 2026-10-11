package com.airtribe.ridewise.service;

import com.airtribe.ridewise.exception.EntityNotFoundException;
import com.airtribe.ridewise.exception.InvalidDataException;
import com.airtribe.ridewise.model.Rider;
import com.airtribe.ridewise.util.DataStore;

import java.util.List;

public class RiderService {
    private final DataStore<Rider> riderStore;

    public RiderService() {
        this.riderStore = new DataStore<>();
    }

    public void register(Rider rider) {
        if (!riderStore.saveIfAbsent(rider)) {
            throw new InvalidDataException("Rider id already registered: " + rider.getId());
        }
    }

    public Rider findById(String id) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("Rider id cannot be blank");
        }

        Rider rider = riderStore.findById(id);
        if (rider == null) {
            throw new EntityNotFoundException("Rider not found: " + id);
        }

        return rider;
    }

    public List<Rider> findAll() {
        return riderStore.findAll();
    }
}
