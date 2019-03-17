package net.opendatadev;

import java.util.List;

/**
 *
 */
public class Manifest
{
    /**
     *
     */
    private int version;

    /**
     *
     */
    private List<Dataset> datasets;

    /**
     * @return
     */
    public int getVersion()
    {
        return version;
    }

    /**
     * @return
     */
    public List<Dataset> getDatasets()
    {
        return datasets;
    }

    /**
     *
     */
    public static class Dataset
    {
        /**
         *
         */
        private String name;

        /**
         *
         */
        private String country;

        /**
         *
         */
        private String subdivision;

        /**
         *
         */
        private String region;

        /**
         *
         */
        private String city;

        /**
         *
         */
        private String provider;

        /**
         *
         */
        private String id;

        /**
         *
         */
        private List<Download> downloads;

        /**
         * @return
         */
        public String getName()
        {
            return name;
        }

        /**
         * @return
         */
        public String getCountry()
        {
            return country;
        }

        /**
         * @return
         */
        public String getSubdivision()
        {
            return subdivision;
        }

        /**
         * @return
         */
        public String getRegion()
        {
            return region;
        }

        /**
         * @return
         */
        public String getCity()
        {
            return city;
        }

        /**
         * @return
         */
        public String getProvider()
        {
            return provider;
        }

        /**
         * @return
         */
        public String getId()
        {
            return id;
        }

        /**
         * @return
         */
        public List<Download> getDownloads()
        {
            return downloads;
        }

        /**
         *
         */
        public static class Download
        {
            /**
             *
             */
            private String src;

            /**
             *
             */
            private String encoding;

            /**
             * @return
             */
            public String getSrc()
            {
                return src;
            }

            /**
             * @return
             */
            public String getEncoding()
            {
                return encoding;
            }
        }
    }
}
