package org.litesoft.deploypackagedversion;

import org.litesoft.packageversioned.*;
import org.litesoft.server.file.*;
import org.litesoft.server.util.*;

import java8.util.function.*;

import java.io.*;
import java.util.*;

/**
 * Four Parameters are needed (Keys for the Arguments):
 * - Target ("Target") e.g. "jre"
 * - Bucket ("Bucket") - Bucket to Publish into (See ParameterBucket for details).
 * - Version ("Version") - optional will be selected from the latest 'zip' file found &
 * - LocalVerDir ("LocalVerDir") - See ParameterLocalVerDir for details.
 * <p/>
 * In addition, the DeploymentGroup will automatically be selected from the first entry of the "DeploymentGroupSet"
 * file (See ParameterDeploymentGroup & DeploymentGroupSet for details).
 * <p/>
 * As each Argument key starts w/ a unique letter, the 'permutations' option is active.
 * Any non-keyed values are applied in the order above (excess keyed entries are noted, excess non-keyed entries are an Error)
 */
public class Parameters extends AbstractParametersS3 {
    private ParameterLocalVerDir mLocalVerDir = ParameterLocalVerDir.existing();

    private Parameter<?>[] mParameters = {mTarget, mBucket, mVersion, mLocalVerDir};

    public Parameters( ArgsToMap pArgs ) {
        prepToString( mBucket, mTarget, mVersion, "To:", mDeploymentGroup );
        populate( mParameters, pArgs );
    }

    public final String getVersion() {
        return super.getVersion();
    }

    public File getLocalVerDir() {
        return mLocalVerDir.get();
    }

    @Override
    public boolean validate() {
        if ( mTarget.validate() && mBucket.validate() && mLocalVerDir.validate() ) {
            mVersion.setIfNull( new Supplier<String>() {
                @Override
                public String get() {
                    return extractLatestVersion();
                }
            } );
        }
        if ( validate( mParameters ) ) {
            mDeploymentGroup.set( DeploymentGroupSet.get().first() ); // Auto Set the DeploymentGroup
            return true;
        }
        return false;
    }

    private String extractLatestVersion() {
        List<String> zZipFileNames = mTarget.getTargetZipFileNames( new FilePersister( getLocalVerDir() ) );
        if ( zZipFileNames.isEmpty() ) {
            return null;
        }
        Collections.sort( zZipFileNames );
        return zZipFileNames.get( zZipFileNames.size() - 1 );
    }
}
