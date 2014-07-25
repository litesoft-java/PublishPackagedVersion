package org.litesoft.publishpackagedversion;

import org.litesoft.packageversioned.*;
import org.litesoft.server.file.*;
import org.litesoft.server.util.*;

import java8.util.function.*;

import java.io.*;
import java.util.*;

/**
 * Four Parameters are needed (Keys for the Arguments):
 * - Target ("Target") e.g. "jre"
 * - Bucket ("BucketURL") - Bucket URL to Publish into (See AbstractParameters for details).
 * - Version ("Version") - optional will be selected from the latest 'zip' file found &
 * - LocalVerDir ("LocalVerDir") - See AbstractParameters for details.
 * <p/>
 * In addition, the DeploymentGroup will automatically be selected from the first entry of the "DeploymentGroupSet"
 * file (See AbstractParameters for details).
 * <p/>
 * As each Argument key starts w/ a unique letter, the 'permutations' option is active.
 * Any non-keyed values are applied in the order above (excess keyed entries are noted, excess non-keyed entries are an Error)
 */
public class Parameters extends AbstractParameters {
    private ParameterTarget mTarget = new ParameterTarget();
    private ParameterBucketURL mBucketURL = new ParameterBucketURL();
    private ParameterVersion mVersion = new ParameterVersion();
    private ParameterLocalVerDir mLocalVerDir = new ParameterLocalVerDir();

    private Parameter<?>[] mParameters = {mTarget, mBucketURL, mVersion, mLocalVerDir};

    private ParameterDeploymentGroup mDeploymentGroup = new ParameterDeploymentGroup();

    public Parameters( ArgsToMap pArgs ) {
        populate( mParameters, pArgs );
    }

    public final String getTarget() {
        return mTarget.get();
    }

    public String getBucketURL() {
        return mBucketURL.get();
    }

    public final String getVersion() {
        return mVersion.get();
    }

    public File getLocalVerDir() {
        return mLocalVerDir.get();
    }

    public String getDeploymentGroup() {
        return mDeploymentGroup.get();
    }

    @Override
    public boolean validate() {
        if ( mTarget.validate() && mBucketURL.validate() && mLocalVerDir.validate() ) {
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
