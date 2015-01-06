package org.shunya.util;

import java.util.ArrayList;
import java.util.List;

public class Permutations {
    private boolean[] used;
    private StringBuilder out = new StringBuilder();
    private final String in;
    List<String> results = new ArrayList<>();
    public Permutations( final String str ){
        in = str;
        used = new boolean[ in.length() ];
    }
    public void permute( ){
        if( out.length() == in.length() ){
            results.add(out.toString());
            System.out.println( out );
            return;
        }
        for( int i = 0; i < in.length(); ++i ){
            if( used[i] ) continue;
            out.append( in.charAt(i) );
            used[i] = true;
            permute();
            used[i] = false;
            out.setLength( out.length() - 1 );
        }
    }

    public static void main(String[] args) {
        Permutations test = new Permutations("AmanMunishJaskirat");
        test.permute();
    }
}
