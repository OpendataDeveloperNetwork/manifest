package net.opendatadev;

import java.util.List;

public class Manifest
{
    private int           version;
    private List<Dataset> datasets;

    public int getVersion()
    {
        return version;
    }

    public List<Dataset> getDatasets()
    {
        return datasets;
    }

    public static class Dataset
    {
        private String         name;
        private String         country;
        private String         subdivision;
        private String         region;
        private String         city;
        private String         provider;
        private String         id;
        private List<Download> downloads;

        public String getName()
        {
            return name;
        }

        public String getCountry()
        {
            return country;
        }

        public String getSubdivision()
        {
            return subdivision;
        }

        public String getRegion()
        {
            return region;
        }

        public String getCity()
        {
            return city;
        }

        public String getProvider()
        {
            return provider;
        }

        public String getId()
        {
            return id;
        }

        public List<Download> getDownloads()
        {
            return downloads;
        }

        public static class Download
        {
            private String src;
            private String encoding;

            public String getSrc()
            {
                return src;
            }

            public String getEncoding()
            {
                return encoding;
            }
        }
    }
}
