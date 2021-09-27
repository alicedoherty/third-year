{-# LANGUAGE CPP #-}
{-# LANGUAGE NoRebindableSyntax #-}
{-# OPTIONS_GHC -fno-warn-missing-import-lists #-}
module Paths_ex00 (
    version,
    getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir,
    getDataFileName, getSysconfDir
  ) where

import qualified Control.Exception as Exception
import Data.Version (Version(..))
import System.Environment (getEnv)
import Prelude

#if defined(VERSION_base)

#if MIN_VERSION_base(4,0,0)
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#else
catchIO :: IO a -> (Exception.Exception -> IO a) -> IO a
#endif

#else
catchIO :: IO a -> (Exception.IOException -> IO a) -> IO a
#endif
catchIO = Exception.catch

version :: Version
version = Version [0,1,0,0] []
bindir, libdir, dynlibdir, datadir, libexecdir, sysconfdir :: FilePath

bindir     = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\bin"
libdir     = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\lib\\x86_64-windows-ghc-8.6.4\\ex00-0.1.0.0-5j8r5Ur4R3S3Ks9zUVejSd-ex00"
dynlibdir  = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\lib\\x86_64-windows-ghc-8.6.4"
datadir    = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\share\\x86_64-windows-ghc-8.6.4\\ex00-0.1.0.0"
libexecdir = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\libexec\\x86_64-windows-ghc-8.6.4\\ex00-0.1.0.0"
sysconfdir = "C:\\Users\\Alice\\Google Drive\\TCD\\Third Year\\Semester 1\\Functional Programming\\Exercises\\Exercise00\\Exercise00\\.stack-work\\install\\5f8342fd\\etc"

getBinDir, getLibDir, getDynLibDir, getDataDir, getLibexecDir, getSysconfDir :: IO FilePath
getBinDir = catchIO (getEnv "ex00_bindir") (\_ -> return bindir)
getLibDir = catchIO (getEnv "ex00_libdir") (\_ -> return libdir)
getDynLibDir = catchIO (getEnv "ex00_dynlibdir") (\_ -> return dynlibdir)
getDataDir = catchIO (getEnv "ex00_datadir") (\_ -> return datadir)
getLibexecDir = catchIO (getEnv "ex00_libexecdir") (\_ -> return libexecdir)
getSysconfDir = catchIO (getEnv "ex00_sysconfdir") (\_ -> return sysconfdir)

getDataFileName :: FilePath -> IO FilePath
getDataFileName name = do
  dir <- getDataDir
  return (dir ++ "\\" ++ name)
