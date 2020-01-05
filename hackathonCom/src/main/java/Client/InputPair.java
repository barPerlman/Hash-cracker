package Client;

public class InputPair {
    private String hash;
    private int hashLen;

    public String getHash() {
        return hash;
    }

    public int getHashLen() {
        return hashLen;
    }

    public InputPair(String hash, int hashLen) {
        this.hash = hash;
        this.hashLen = hashLen;
    }
}
