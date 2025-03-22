using FireSharp;
using FireSharp.Config;
using FireSharp.Interfaces;
using System.IO;

namespace APIService.Services
{

    public static class FirebaseConfigSetup
    {
        public static IFirebaseClient GetClient()
        {
            var auth = "wlfyxMSdsTN1Zw2cMSaX86QgJeuXLvMlrDukKioE";
            var config = new FirebaseConfig
            {
                AuthSecret = auth,
                BasePath = "https://shopii-f87bc-default-rtdb.asia-southeast1.firebasedatabase.app/"
            };
            return new FirebaseClient(config);
        }
    }
}
