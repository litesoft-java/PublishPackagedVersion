package org.litesoft.deploypackagedversion;

import org.litesoft.commonfoundation.exceptions.*;
import org.litesoft.commonfoundation.typeutils.*;
import org.litesoft.packageversioned.*;
import org.litesoft.server.file.*;
import org.litesoft.server.util.*;

import java.io.*;

public class DeployPackagedVersion extends AbstractAppS3<Parameters> {
    public static final String VERSION = "0.9";

    public DeployPackagedVersion( Parameters pParameters ) {
        super( "Deploy", pParameters );
    }

    public static void main( String[] args ) {
        CONSOLE.printLn( "DeployPackagedVersion vs " + VERSION );
        new DeployPackagedVersion( new Parameters( new ArgsToMap( args ) ) ).run();
    }

    protected void process() {
        new Publisher( createPersister() ).process();
    }

    protected class Publisher extends Processor {
        public Publisher( Persister pPersister ) {
            super( pPersister );
        }

        public void process() {
            transferZipFile();
            writeDeploymentGroupVersionFiles();
        }

        public void transferZipFile() {
            File zFromFile = new File( mParameters.getLocalVerDir(), Paths.forwardSlashCombine( getTarget(), getVersion() + ".zip" ) );
            if ( !zFromFile.isFile() ) {
                throw new FileSystemException( "File Not Found: " + zFromFile );
            }
            String zPath = createPath( getVersion() + ".zip" );
            CONSOLE.printLn( "Copy: " + zFromFile + " -> " + zPath );
            mPersister.putFile( zPath, FileUtils.asInputStream( zFromFile ) );
        }
    }
}
