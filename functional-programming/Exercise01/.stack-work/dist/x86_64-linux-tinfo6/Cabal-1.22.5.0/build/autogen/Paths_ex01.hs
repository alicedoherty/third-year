module Paths_ex01 (
    version,
    getBinDir, getLibDir, getDataDir, getLibexecDir,
    getDataFileName, getSysconfDir
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
catchIO = Exception.catch

version :: Version
version = Version [0,1,0,0] []
bindir, libdir, datadir, libexecdir, sysconfdir :: FilePath

bindir     = "/mnt/c/Users/Alice/Google Drive/GitHub/third-year/functional-programming/Exercise01/.stack-work/install/x86_64-linux-tinfo6/19ea852e707d0e63a4e41e6e4c6eeb5a0a9b2c2fa8c66b4d8c51eb3a4b66fe23/7.10.3/bin"
libdir     = "/mnt/c/Users/Alice/Google Drive/GitHub/third-year/functional-programming/Exercise01/.stack-work/install/x86_64-linux-tinfo6/19ea852e707d0e63a4e41e6e4c6eeb5a0a9b2c2fa8c66b4d8c51eb3a4b66fe23/7.10.3/lib/x86_64-linux-ghc-7.10.3/ex01-0.1.0.0-CmkBIqRQqyCJ0gZevCCGZy"
datadir    = "/mnt/c/Users/Alice/Google Drive/GitHub/third-year/functional-programming/Exercise01/.stack-work/install/x86_64-linux-tinfo6/19ea852e707d0e63a4e41e6e4c6eeb5a0a9b2c2fa8c66b4d8c51eb3a4b66fe23/7.10.3/share/x86_64-linux-ghc-7.10.3/ex01-0.1.0.0"
libexecdir = "/mnt/c/Users/Alice/Google Drive/GitHub/third-year/functional-programming/Exercise01/.stack-work/install/x86_64-linux-tinfo6/19ea852e707d0e63a4e41e6e4c6eeb5a0a9b2c2fa8c66b4d8c51eb3a4b66fe23/7.10.3/libexec"
sysconfdir = "/mnt/c/Users/Alice/Google Drive/GitHub/third-year/functional-programming/Exercise01/.stack-work/install/x86_64-linux-tinfo6/19ea852e707d0e63a4e41e6e4c6eeb5a0a9b2c2fa8c66b4d8c51eb3a4b66fe23/7.10.3/etc"

getBinDir, getLibDir, getDataDir, getLibexecDir, getSysconfDir :: IO FilePath
getBinDir = catchIO (getEnv "ex01_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "ex01_libdir") (\_ -> return libdir)
getDataDir = catchIO (getEnv "ex01_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "ex01_libexecdir") (\_ -> return libexecdir)
getSysconfDir = catchIO (getEnv "ex01_sysconfdir") (\_ -> return sysconfdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "/" ++ name)
