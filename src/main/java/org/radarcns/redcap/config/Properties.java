package org.radarcns.redcap.config;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import okhttp3.Credentials;
import org.radarcns.config.YamlConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Copyright 2017 King's College London
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class Properties {

    /** Logger. **/
    private static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);

    /** Path to the configuration file for AWS deploy. **/
    private static final String PATH_FILE_AWS = "/usr/share/tomcat8/conf/";

    /** Path to the configuration file for Docker image. **/
    private static final String PATH_FILE_DOCKER = "/usr/local/tomcat/conf/radar/";

    /** Placeholder alternative path for the config folder. **/
    private static final String CONFIG_FOLDER = "CONFIG_FOLDER";

    /** API Config file name. **/
    public static final String NAME_CONFIG_FILE = "redcap_instances.yml";

    /** Path where the config file is located. **/
    //private static String validPath;

    private static final Configuration CONFIG;

    static {
        try {
            CONFIG = loadApiConfig();
        } catch (IOException exec) {
            LOGGER.error(exec.getMessage(), exec);
            throw new ExceptionInInitializerError(exec);
        }
    }

    private Properties() {
        //Nothing to do
    }

    /**
     * Loads the API configuration file. First of all, the {@code CONFIG_FOLDER} env variable is
     *      checked to verify if points a valid config file. If not, the default location for AWS
     *      and Docker image deployment are checked. In the last instance, the config file is
     *      searched inside the default projects resources folder.
     */
    private static Configuration loadApiConfig() throws IOException {
        String[] paths = new String[]{
            System.getenv(CONFIG_FOLDER),
            PATH_FILE_AWS,
            PATH_FILE_DOCKER
        };

        Configuration config;
        for (int i = 0; i < paths.length; i++) {
            config = loadApiConfig(paths[i]);
            if (config != null) {
                return config;
            }
        }

        String path = Properties.class.getClassLoader().getResource(NAME_CONFIG_FILE).getFile();
        //validPath = new File(path).getParent() + "/";

        LOGGER.info("Loading Config file located at : {}", path);

        return new YamlConfigLoader().load(new File(path), Configuration.class);
    }

    private static Configuration loadApiConfig(String path) throws IOException {
        //validPath = path;
        String filePath = path + NAME_CONFIG_FILE;

        if (checkFileExist(filePath)) {
            LOGGER.info("Loading Config file located at : {}", path);
            return new YamlConfigLoader().load(new File(filePath), Configuration.class);
        }

        //validPath = null;
        return null;
    }

    /**
     * Checks whether the give path points a file.
     *
     * @param path that should point a file
     * @return true if {@code path} points a file, false otherwise
     */
    private static boolean checkFileExist(String path) {
        return path == null ? false : new File(path).exists();
    }

    public static void validate() {
        //Nothing to do
    }

    protected static boolean isSupportedInstance(URL url, Integer projectId) {
        return CONFIG.getServers().contains(new RedCapInfo(url, projectId));
    }

    protected static RedCapInfo getRedCapInfo(URL url, Integer projectId) {
        RedCapInfo identifier = new RedCapInfo(url, projectId);
        for (RedCapInfo info : CONFIG.getServers()) {
            if (info.equals(identifier)) {
                return info;
            }
        }

        throw new IllegalArgumentException("No project " + projectId + " for instance "
                + url.toString());
    }

    /**
     * Relying on {@link Credentials#basic(String, String)}, it generates and returns the
     *      credentials required to refresh OAuth2 token. The client identifier and client secret
     *      are provided by {@link Configuration}.
     * @return {@link String} representing OAuth2 credentials to refresh the token
     */
    public static String getOauthCredential() {
        return Credentials.basic(CONFIG.getOauthClientId(), CONFIG.getOauthClientSecret());
    }

    /**
     * Generates the token end point {@link URL} needed to refresh tokens against Management Portal.
     * @return {@link URL} useful to refresh tokens
     * @throws MalformedURLException in case the {@link URL} cannot be generated
     */
    public static URL getTokenEndPoint() throws MalformedURLException {
        return new URL(CONFIG.getManagementPortalUrl(), CONFIG.getTokenEndpoint());
    }

    /**
     * Generates the token end point {@link URL} needed to manage subjects on Management Portal.
     * @return {@link URL} useful create and update subjects
     * @throws MalformedURLException in case the {@link URL} cannot be generated
     */
    public static URL getSubjectEndPoint() throws MalformedURLException {
        return new URL(CONFIG.getManagementPortalUrl(), CONFIG.getSubjectEndpoint());
    }

    /**
     * Generates the token end point {@link URL} needed to reade projects on Management Portal.
     * @return {@link URL} useful to read project information
     * @throws MalformedURLException in case the {@link URL} cannot be generated
     */
    public static URL getProjectEndPoint() throws MalformedURLException {
        return new URL(CONFIG.getManagementPortalUrl(), CONFIG.getProjectEndpoint());
    }
}