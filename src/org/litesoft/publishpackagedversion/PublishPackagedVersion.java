package org.litesoft.publishpackagedversion;

import org.litesoft.aws.s3.*;
import org.litesoft.commonfoundation.exceptions.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.packageversioned.*;
import org.litesoft.server.file.*;
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

    private Persister createPersister()
            throws IOException {
        ParameterBucket zBucket = mParameters.getParameterBucket();
        return new S3Persister( BucketCredentials.get( zBucket.get() ), new Bucket( zBucket.getS3Endpoint(), zBucket.get() ) );
    }

    private void process()
            throws IOException {
        System.out.println( "Deploy '" + getTarget() + "' vs '" + getVersion() + "' To: " +
                            getDeploymentGroup() + " (Bucket: " + mParameters.getBucket() + ")" );
        new Publisher( createPersister() ).process();
    }

    private String getVersion() {
        return mParameters.getVersion();
    }

    private String getTarget() {
        return mParameters.getTarget();
    }

    private String getDeploymentGroup() {
        return mParameters.getDeploymentGroup();
    }

    protected class Publisher {
        protected final Persister mPersister;

        public Publisher( Persister pPersister ) {
            mPersister = pPersister;
        }

        public void process() {
            transferZipFile();
            createDeploymentGroupVersionFile( "-" + getVersion() );
            createDeploymentGroupVersionFile( "" );
        }

        private void createDeploymentGroupVersionFile( String pSpecificVersionSuffix ) {
            String zPath = createPath( getDeploymentGroup() + pSpecificVersionSuffix + ".txt" );
            System.out.println( "  Writing: " + zPath );
            mPersister.putTextFile( zPath, Strings.toLines( getVersion() ) );
        }

        private String createPath( String pFileName ) {
            return "versioned/" + getTarget() + "/" + pFileName;
        }

        public void transferZipFile() {
            File zFromFile = new File( mParameters.getLocalVerDir(), Paths.forwardSlashCombine( getTarget(), getVersion() + ".zip" ) );
            if ( !zFromFile.isFile() ) {
                throw new FileSystemException( "File Not Found: " + zFromFile );
            }
            String zPath = createPath( getVersion() + ".zip" );
            System.out.println( "  Copy: " + zFromFile + " -> " + zPath );
            mPersister.putFile( zPath, FileUtils.asInputStream( zFromFile ) );
        }
    }
}
