// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Provide operations on the paths of virtual files and projects.
 */
public class Pathing {
    /**
     * Translate the path with slashes as separators ("/") to a system-dependent separators.
     *
     * @param path to be translated
     * @return system-dependent path
     */
    static public @Nullable
    String systemDependentPath(@Nullable String path) {
        if (path == null) {
            return null;
        }

        return path.replace('/', File.separatorChar);
    }

    /**
     * Replace the base path with "." if path prefixed by it.
     *
     * @param path     to some file
     * @param basePath base path of a project
     * @return path with base path stripped to "." if prefixed by it; otherwise just the path
     */
    static public
    @Nullable
    String stripBaseBath(@Nullable String path, @Nullable String basePath) {
        if (path == null) {
            return null;
        }

        if (basePath == null) {
            return path;
        }

        if (path.startsWith(basePath)) {
            return "." + path.substring(basePath.length());
        }
        return path;
    }
}
