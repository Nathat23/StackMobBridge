package uk.antiperson.stackmobbridge.cache;

public abstract class StackStorage implements StorageMethod {

    private StorageManager storageManager;
    public StackStorage(StorageManager storageManager){
        this.storageManager = storageManager;
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }

}
