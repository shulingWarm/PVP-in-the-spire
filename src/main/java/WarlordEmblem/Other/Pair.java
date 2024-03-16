package WarlordEmblem.Other;

//类似于C++的std::pair
public class Pair<K,V> {
    public K first;
    public V second;

    public Pair()
    {

    }

    public Pair(K first,V second)
    {
        this.first = first;
        this.second = second;
    }

}
