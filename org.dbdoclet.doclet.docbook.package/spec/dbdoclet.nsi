; dbdoclet-doclet.nsi
;--------------------------------

;Include Modern UI

!include "MUI.nsh"

; The name of the installer
Name "dbdoclet"

; The file to write
OutFile @OutFile@

; The default installation directory
InstallDir "$PROGRAMFILES\DocBook Doclet\doclet"

;--------------------------------
;Interface Settings

!define MUI_ABORTWARNING
!define MUI_ICON "root\Programme\dbdoclet\icons\48x48\dbdoclet.ico"
!define MUI_UNICON "root\Programme\dbdoclet\icons\48x48\dbdoclet.ico"

;--------------------------------
;Pages

!insertmacro MUI_PAGE_LICENSE "root\Programme\dbdoclet\COPYING"
; !insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
  
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
;Languages

!insertmacro MUI_LANGUAGE "Arabic"
!insertmacro MUI_LANGUAGE "Bulgarian"
!insertmacro MUI_LANGUAGE "Croatian"
!insertmacro MUI_LANGUAGE "Czech"
!insertmacro MUI_LANGUAGE "Danish"
!insertmacro MUI_LANGUAGE "Dutch"
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Estonian"
!insertmacro MUI_LANGUAGE "Farsi"
!insertmacro MUI_LANGUAGE "Finnish"
!insertmacro MUI_LANGUAGE "French"
!insertmacro MUI_LANGUAGE "German"
!insertmacro MUI_LANGUAGE "Greek"
!insertmacro MUI_LANGUAGE "Hebrew"
!insertmacro MUI_LANGUAGE "Hungarian"
!insertmacro MUI_LANGUAGE "Indonesian"
!insertmacro MUI_LANGUAGE "Italian"
!insertmacro MUI_LANGUAGE "Japanese"
!insertmacro MUI_LANGUAGE "Korean"
!insertmacro MUI_LANGUAGE "Latvian"
!insertmacro MUI_LANGUAGE "Lithuanian"
!insertmacro MUI_LANGUAGE "Macedonian"
!insertmacro MUI_LANGUAGE "Norwegian"
!insertmacro MUI_LANGUAGE "Polish"
!insertmacro MUI_LANGUAGE "Portuguese"
!insertmacro MUI_LANGUAGE "PortugueseBR"
!insertmacro MUI_LANGUAGE "Romanian"
!insertmacro MUI_LANGUAGE "Russian"
!insertmacro MUI_LANGUAGE "Serbian"
!insertmacro MUI_LANGUAGE "SimpChinese"
!insertmacro MUI_LANGUAGE "Slovak"
!insertmacro MUI_LANGUAGE "Slovenian"
!insertmacro MUI_LANGUAGE "Spanish"
!insertmacro MUI_LANGUAGE "Swedish"
!insertmacro MUI_LANGUAGE "Thai"
!insertmacro MUI_LANGUAGE "TradChinese"
!insertmacro MUI_LANGUAGE "Turkish"
!insertmacro MUI_LANGUAGE "Ukrainian"
 
;--------------------------------

; The stuff to install
Section dbdoclet

  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  SetShellVarContext "all"

  CreateDirectory "$SMPROGRAMS\dbdoclet"

  ; CreateShortCut '$DESKTOP\dbdoclet.lnk' "$INSTDIR\doc\html\index.html" "" '$INSTDIR\icons\48x48\dbdoclet.ico'
  CreateShortCut '$SMPROGRAMS\dbdoclet\dbdoclet (HTML).lnk' "$INSTDIR\doc\manpage.html" "" '$INSTDIR\icons\48x48\dbdoclet.ico'
  CreateShortCut '$SMPROGRAMS\dbdoclet\dbdoclet (PDF).lnk' "$INSTDIR\doc\manpage.pdf" "" '$INSTDIR\icons\48x48\dbdoclet.ico'
  CreateShortCut "$SMPROGRAMS\dbdoclet\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0

  File /r "root\Programme\dbdoclet\*.*"
  
  FileOpen  $0 "$INSTDIR\bin\dbdoclet.bat" w
  FileWrite $0 "@echo off$\r$\n"
  FileWrite $0 "set DBDOCLET_HOME=$INSTDIR$\r$\n"
  FileWrite $0 "javadoc -J-Xmx1024m -docletpath $\"%DBDOCLET_HOME%\jars\dbdoclet_@Version@.jar$\" -doclet org.dbdoclet.doclet.docbook.DocBookDoclet %*$\r$\n"
  FileClose $0

  FileOpen  $1 "$SYSDIR\dbdoclet.bat" w
  FileWrite $1 "@echo off$\r$\n"
  FileWrite $1 "set DBDOCLET_HOME=$INSTDIR$\r$\n"
  FileWrite $0 "java -J-Xmx1024m -docletpath $\"%DBDOCLET_HOME%\jars\dbdoclet_@Version@.jar$\" -doclet org.dbdoclet.doclet.docbook.DocBookDoclet %*$\r$\n"
  FileClose $1
 
  WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\dbdoclet" "DisplayName" "dbdoclet"
  WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\dbdoclet" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteUninstaller "Uninstall.exe"

SectionEnd 

; Uninstall section

Section Uninstall
    
    SetShellVarContext "all"

    Delete '$DESKTOP\dbdoclet.lnk'
    RMDir /r "$SMPROGRAMS\dbdoclet"
    RMDir /r "$INSTDIR"

SectionEnd

Function .onInit

  !insertmacro MUI_LANGDLL_DISPLAY

FunctionEnd

Function Preinstall

  ReadRegStr $R0 HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\dbdoclet" "UninstallString"
  Done:

FunctionEnd
