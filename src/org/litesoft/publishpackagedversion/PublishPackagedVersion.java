package org.litesoft.publishpackagedversion;

import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.server.util.*;

import java.io.*;

public class PublishPackagedVersion {
    public static final String VERSION = "0.9";

    private Parameters mParameters;

    public PublishPackagedVersion( Parameters pParameters ) {
        if ( !(mParameters = pParameters).validate() ) {
            System.exit( 1 );
        }
    }

    public static void main( String[] args )
            throws Exception {
        System.out.println( "PublishPackagedVersion vs " + VERSION );
        new PublishPackagedVersion( new Parameters( new ArgsToMap( args ) ) ).process();
        System.out.println( "Done!" );
    }

    private void process()
            throws IOException {
        String zDeploymentGroup = mParameters.getDeploymentGroup();
        String zTarget = mParameters.getTarget();
        String zVersion = mParameters.getVersion();
        File zFromFile = new File( mParameters.getLocalVerDir(), Paths.forwardSlashCombine( zTarget, zVersion + ".zip" ) );
        String zToFile = Paths.forwardSlashCombine( mParameters.getBucketURL(), zTarget, zVersion + ".zip" );
        System.out.println( "  Deploy To: " + zDeploymentGroup );
        System.out.println( "       Copy: " + zFromFile.getPath() );
        System.out.println( "         To: " + zToFile );
        // ToDo: XXX
    }
}
