package ru.itmo.java;

public class HashTable {

    private final double loadFactor;

    private int threshold;
    private int size;
    private int capacity;

    private Entry[] data;


    public HashTable(int initialCapacity, double loadFactor) {
        this.capacity = initialCapacity;

        int twoDegreeCapacity = 1;
        while (true) {
            if (twoDegreeCapacity >= capacity) {
                capacity = twoDegreeCapacity;
                break;
            }
            twoDegreeCapacity *= 2;
        }

        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        this.size = 0;

        data = new Entry[capacity];
    }

    public HashTable(int initialCapacity) {
        this(initialCapacity, 0.5);
    }

    public HashTable() {
        this(512, 0.5);
    }

    public Object put(Object key, Object value) {
        Object returnRef = null;

        int index = find(data, key,true);
        if (index != -1) {
            returnRef = data[index].value;
            data[index].value = value;
        }
        else {
            index = find(data, key,false);
            data[index] = new Entry(key, value);
            size++;
        }

        if (size >= threshold) {
            resize();
        }

        return returnRef;
    }

    public Object get(Object key) {
        int index = find(data, key, true);
        if (index != -1) {
            return data[index].value;
        }
        else {
            return null;
        }
    }

    public Object remove(Object key) {
        int index = find(data, key, true);
        if (index != -1) {
            data[index].deleted = true;
            size--;
            return data[index].value;
        }
        else {
            return null;
        }
    }

    public int size() {
        return size;
    }



    private int getBaseHash(Object key) {
        return Math.abs(key.hashCode());
    }

    private int find(Entry[] array, Object key, boolean checkExisting) {
        int baseHash = getBaseHash(key);
        int searchInterval = 0;

        while (true) {
            searchInterval++;
            int currentHash = (baseHash + searchInterval * searchInterval) % array.length;
            if (array[currentHash] == null) {
                if (checkExisting) {
                    return -1;
                }
                else {
                    return currentHash;
                }
            }
            else if (array[currentHash].key.equals(key)) {
                if (checkExisting) {
                    if (!array[currentHash].deleted) {
                        return currentHash;
                    }
                    else {
                        return -1;
                    }
                }
                else {
                    return currentHash;
                }
            }
            else if (!checkExisting && array[currentHash].deleted) {
                return currentHash;
            }
        }

    }

    private void resize() {
        capacity *= 2;
        threshold = (int) (capacity * loadFactor);
        size = 0;
        Entry[] tempArray = new Entry[capacity];

        for (int i = 0; i < data.length; i++) {
            Entry entry = data[i];

            if (entry == null || entry.deleted) {
                data[i] = null;
                continue;
            }

            int index = find(tempArray, entry.key, false);
            if (index != -1) {
                tempArray[index] = entry;
                size++;
            }

            data[i] = null;
        }

        data = tempArray;
    }

    private static class Entry {

        private Object key;
        private Object value;

        private boolean deleted = false;

        public Entry(Object key, Object value) {
            this.key = key;
            this.value = value;
        }
    }
}