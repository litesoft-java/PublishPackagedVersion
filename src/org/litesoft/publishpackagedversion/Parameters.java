package org.litesoft.publishpackagedversion;

import org.litesoft.packageversioned.*;
import org.litesoft.server.file.*;
import org.litesoft.server.util.*;

import java.io.*;
import java.util.*;

/**
 * Four Parameters are needed (Keys for the Arguments):
 * - Target ("Target") e.g. "jre"
 * - Bucket ("BucketURL") - Bucket URL to Publish into (See AbstractParameters for details).
 * - Version ("Version") - optional will be selected from the latest 'zip' file found &
 * - LocalVerDir ("LocalVerDir") - See AbstractParameters for details.
 * <p/>
 * <p/>
 * As each Argument key starts w/ a unique letter, the 'permutations' option is active.
 * Any non-keyed values are applied in the order above (excess keyed entries are noted, excess non-keyed entries are an Error)
 */
public class Parameters extends AbstractParameters {
    public Parameters( String pTarget ) {
        setTarget( pTarget );
    }

    @Override
    public String getBucketURL() {
        return super.getBucketURL();
    }

    @Override
    public File getLocalVerDir() {
        return super.getLocalVerDir();
    }

    @Override
    public boolean validate() {
        if ( !(validateBucketUrl() & validateLocalVerDir()) ) {
            return false;
        }
        if ( getVersion() == null ) {
            setVersionOptionally( extractLatestVersion() );
        }
        return validateVersion();
    }

    public Parameters bucketURL( String pbucketURL ) {
        return setBucketURL( pbucketURL );
    }

    public Parameters version( String pVersion ) {
        return setVersionOptionally( pVersion );
    }

    public Parameters localVerDir( String pLocalVerDir ) {
        return setLocalVerDir( pLocalVerDir );
    }

    public static Parameters from( ArgsToMap pArgs ) {
        return finish( pArgs,
                       new Parameters( getTargetFrom( pArgs ) )
                               .bucketURL( getBucketURLFrom( pArgs ) )
                               .version( getVersionFrom( pArgs ) )
                               .localVerDir( getLocalVerDirFrom( pArgs ) ) );
    }

    private String extractLatestVersion() {
        List<String> zZipFileNames = getTargetZipFileNames( new FilePersister( getLocalVerDir() ) );
        if ( zZipFileNames.isEmpty() ) {
            return null;
        }
        Collections.sort( zZipFileNames );
        return zZipFileNames.get( zZipFileNames.size() - 1 );
    }
}
