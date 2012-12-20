package com.hornmicro.discoverer



import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.graphics.ImageData
import org.eclipse.swt.internal.cocoa.NSImage
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Shell
import org.eclipse.swt.widgets.Tree
import org.eclipse.swt.widgets.TreeItem

class MainView extends Composite {
    Tree tree
    Image folder
    Image folderWhite
    Image hdd
    Image hddWhite
    TreeItem lastSelected
    MainView(Composite parent) {
        super(parent, SWT.NONE)
        //setLayout(new GridLayout(5, true))
        setLayout(new FillLayout())
        createView()
    }
    
    void createView() {
//        IconConstants.each { k, v ->
//            Button button = new Button(this, SWT.PUSH)
//            
//            NSImage img = IconFun.getSystemImageForID(v instanceof String ? IconFun.getIconContstant(v): v)
//            Image image = Image.cocoa_new (this.shell.display, SWT.ICON, img)
//            button.text = k
//            button.image = image
//        }
        
        
        //Button button = new Button(this, SWT.PUSH)
        NSImage img = IconFun.getSystemImageForID(IconFun.getIconContstant("sbFd"))
        
        Image image = Image.cocoa_new (this.shell.display, SWT.BITMAP, img)
        ImageData imageData = image.getImageData()
        image.dispose()
        
        IconFun.setAlpha(imageData)
//        byte[] alphaData = imageData.alphaData
//        byte[] copyData = new byte[alphaData.size()]
//        System.arraycopy(alphaData,0,copyData,copyData.size())
//        for(int i = 0; i < copyData.size(); i++) {
//            copyData[i] = ((copyData[i] as int) - 10) as byte
//        }
//        System.arraycopy(copyData,0,alphaData,0,copyData.size())
        
        image = new Image(this.shell.display, imageData)
        
        IconFun.invert(imageData)
        Image imageWhite = new Image(this.shell.display, imageData)
        
        
//        button.text = "hello"
//        button.image = image
//        button.setSize(200, 40)
        
//        Button button = new Button(this, SWT.PUSH)
//        button.text = "hello"
//        
//        NSWorkspace workspace = NSWorkspace.sharedWorkspace()
//        NSImage nsImage = workspace.iconForFileType(NSString.stringWith("SidebarGenericFolder.icns"))
//        if (nsImage != null) {
//            NSSize size = new NSSize();
//            size.width = size.height = 16;
//            nsImage.setSize(size);
//            nsImage.retain();
//            Image image = Image.cocoa_new(Display.getCurrent(), SWT.BITMAP, nsImage);
//            button.image = image
//        }

        
        //folder = new Image(display, "gfx/folder-close.png")
        hdd = folder = image
//        new Image(display, "gfx/folder-open.png")
//        folderWhite = new Image(display, "gfx/white/folder-close.png")
//        hddWhite = new Image(display, "gfx/white/folder-open.png")
        
        tree = new Tree (this, SWT.SOURCE_LIST)
        //OS.getIconRefFromTypeInfo(DEFAULT_HEIGHT, DEFAULT_HEIGHT, jniRef, jniRef, DEFAULT_HEIGHT, null)
        TreeItem lastItem
        tree.addSelectionListener(new SelectionAdapter() {
            void widgetSelected(SelectionEvent e) {
              TreeItem ti = (TreeItem) e.item;
              if(lastItem) {
                  lastItem.image = image
              }
              println "setting"
              ti.image = imageWhite
              lastItem = ti
            }
        })
        
        TreeItem devices = new TreeItem (tree, SWT.GROUP_ITEM)
        devices.text = "DEVICES"
        shell.display.asyncExec {   
            devices.expanded = true
        }
        TreeItem item = new TreeItem(devices, SWT.NONE)
        item.text = "/"
        item.image = hdd
        
        for(File folderItem : new File("/").listFiles()) {
            if(folderItem.isDirectory() && !(folderItem.name.startsWith(".")) ) {
                TreeItem folderTree = new TreeItem(item, SWT.NONE)
                folderTree.text = folderItem.name
                folderTree.image = folder
            }
        }
        
        shell.display.asyncExec {
            item.expanded = true
        }
        
    }
    
    static main(args) {
        Display display = new Display()
        Shell shell = new Shell(display)
        new MainView(shell)
        shell.setLayout(new FillLayout())
        shell.setSize(400, 400)
        shell.open()
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep()
        }
        display.dispose()    
    }

    
    static IconConstants = [
        kClipboardIcon                : 'CLIP',
        kClippingUnknownTypeIcon      : 'clpu',
        kClippingPictureTypeIcon      : 'clpp',
        kClippingTextTypeIcon         : 'clpt',
        kClippingSoundTypeIcon        : 'clps',
        kDesktopIcon                  : 'desk',
        kFinderIcon                   : 'FNDR',
        kComputerIcon                 : 'root',
        kFontSuitcaseIcon             : 'FFIL',
        kFullTrashIcon                : 'ftrh',
        kGenericApplicationIcon       : 'APPL',
        kGenericCDROMIcon             : 'cddr',
        kGenericControlPanelIcon      : 'APPC',
        kGenericControlStripModuleIcon : 'sdev',
        kGenericComponentIcon         : 'thng',
        kGenericDeskAccessoryIcon     : 'APPD',
        kGenericDocumentIcon          : 'docu',
        kGenericEditionFileIcon       : 'edtf',
        kGenericExtensionIcon         : 'INIT',
        kGenericFileServerIcon        : 'srvr',
        kGenericFontIcon              : 'ffil',
        kGenericFontScalerIcon        : 'sclr',
        kGenericFloppyIcon            : 'flpy',
        kGenericHardDiskIcon          : 'hdsk',
        kGenericIDiskIcon             : 'idsk',
        kGenericRemovableMediaIcon    : 'rmov',
        kGenericMoverObjectIcon       : 'movr',
        kGenericPCCardIcon            : 'pcmc',
        kGenericPreferencesIcon       : 'pref',
        kGenericQueryDocumentIcon     : 'qery',
        kGenericRAMDiskIcon           : 'ramd',
        kGenericSharedLibaryIcon      : 'shlb',
        kGenericStationeryIcon        : 'sdoc',
        kGenericSuitcaseIcon          : 'suit',
        kGenericURLIcon               : 'gurl',
        kGenericWORMIcon              : 'worm',
        kInternationalResourcesIcon   : 'ifil',
        kKeyboardLayoutIcon           : 'kfil',
        kSoundFileIcon                : 'sfil',
        kSystemSuitcaseIcon           : 'zsys',
        kTrashIcon                    : 'trsh',
        kTrueTypeFontIcon             : 'tfil',
        kTrueTypeFlatFontIcon         : 'sfnt',
        kTrueTypeMultiFlatFontIcon    : 'ttcf',
        kUserIDiskIcon                : 'udsk',
        kUnknownFSObjectIcon          : 'unfs',
        //kInternationResourcesIcon     : kInternationalResourcesIcon /* old name*/
        
        kGenericFolderIcon            : 'fldr',
        kDropFolderIcon               : 'dbox',
        kMountedFolderIcon            : 'mntd',
        kOpenFolderIcon               : 'ofld',
        kOwnedFolderIcon              : 'ownd',
        kPrivateFolderIcon            : 'prvf',
        kSharedFolderIcon             : 'shfl',
        
        
        kInternetLocationHTTPIcon     : 'ilht',
        kInternetLocationFTPIcon      : 'ilft',
        kInternetLocationAppleShareIcon : 'ilaf',
        kInternetLocationAppleTalkZoneIcon : 'ilat',
        kInternetLocationFileIcon     : 'ilfi',
        kInternetLocationMailIcon     : 'ilma',
        kInternetLocationNewsIcon     : 'ilnw',
        kInternetLocationNSLNeighborhoodIcon : 'ilns',
        kInternetLocationGenericIcon  : 'ilge',
        
        kAppearanceFolderIcon         : 'appr',
        kAppleExtrasFolderIcon        : 0x616578C4/*'aexƒ'*/,
        kAppleMenuFolderIcon          : 'amnu',
        kApplicationsFolderIcon       : 'apps',
        kApplicationSupportFolderIcon : 'asup',
        kAssistantsFolderIcon         : 0x617374C4/*'astƒ'*/,
        kColorSyncFolderIcon          : 'prof',
        kContextualMenuItemsFolderIcon : 'cmnu',
        kControlPanelDisabledFolderIcon : 'ctrD',
        kControlPanelFolderIcon       : 'ctrl',
        kControlStripModulesFolderIcon : 0x736476C4/*'sdvƒ'*/,
        kDocumentsFolderIcon          : 'docs',
        kExtensionsDisabledFolderIcon : 'extD',
        kExtensionsFolderIcon         : 'extn',
        kFavoritesFolderIcon          : 'favs',
        kFontsFolderIcon              : 'font',
        kHelpFolderIcon               : (int)0xC4686C70/*'ƒhlp' */,
        kInternetFolderIcon           : 0x696E74C4/*'intƒ'*/,
        kInternetPlugInFolderIcon     : (int)0xC46E6574/*'ƒnet' */,
        kInternetSearchSitesFolderIcon : 'issf',
        kLocalesFolderIcon            : (int)0xC46C6F63/*'ƒloc' */,
        kMacOSReadMeFolderIcon        : 0x6D6F72C4/*'morƒ'*/,
        kPublicFolderIcon             : 'pubf',
        kPreferencesFolderIcon        : 0x707266C4/*'prfƒ'*/,
        kPrinterDescriptionFolderIcon : 'ppdf',
        kPrinterDriverFolderIcon      : (int)0xC4707264/*'ƒprd' */,
        kPrintMonitorFolderIcon       : 'prnt',
        kRecentApplicationsFolderIcon : 'rapp',
        kRecentDocumentsFolderIcon    : 'rdoc',
        kRecentServersFolderIcon      : 'rsrv',
        kScriptingAdditionsFolderIcon : (int)0xC4736372/*'ƒscr' */,
        kSharedLibrariesFolderIcon    : (int)0xC46C6962/*'ƒlib' */,
        kScriptsFolderIcon            : 0x736372C4/*'scrƒ'*/,
        kShutdownItemsDisabledFolderIcon : 'shdD',
        kShutdownItemsFolderIcon      : 'shdf',
        kSpeakableItemsFolder         : 'spki',
        kStartupItemsDisabledFolderIcon : 'strD',
        kStartupItemsFolderIcon       : 'strt',
        kSystemExtensionDisabledFolderIcon : 'macD',
        kSystemFolderIcon             : 'macs',
        kTextEncodingsFolderIcon      : (int)0xC4746578/*'ƒtex' */,
        kUsersFolderIcon              : 0x757372C4/*'usrƒ'*/,
        kUtilitiesFolderIcon          : 0x757469C4/*'utiƒ'*/,
        kVoicesFolderIcon             : 'fvoc',
        
        kToolbarCustomizeIcon         : 'tcus',
        kToolbarDeleteIcon            : 'tdel',
        kToolbarFavoritesIcon         : 'tfav',
        kToolbarHomeIcon              : 'thom',
        kToolbarAdvancedIcon          : 'tbav',
        kToolbarInfoIcon              : 'tbin',
        kToolbarLabelsIcon            : 'tblb',
        kToolbarApplicationsFolderIcon : 'tAps',
        kToolbarDocumentsFolderIcon   : 'tDoc',
        kToolbarMovieFolderIcon       : 'tMov',
        kToolbarMusicFolderIcon       : 'tMus',
        kToolbarPicturesFolderIcon    : 'tPic',
        kToolbarPublicFolderIcon      : 'tPub',
        kToolbarDesktopFolderIcon     : 'tDsk',
        kToolbarDownloadsFolderIcon   : 'tDwn',
        kToolbarLibraryFolderIcon     : 'tLib',
        kToolbarUtilitiesFolderIcon   : 'tUtl',
        kToolbarSitesFolderIcon       : 'tSts',
        
        kAppleLogoIcon                : 'capl',
        kAppleMenuIcon                : 'sapl',
        kBackwardArrowIcon            : 'baro',
        kFavoriteItemsIcon            : 'favr',
        kForwardArrowIcon             : 'faro',
        kGridIcon                     : 'grid',
        kHelpIcon                     : 'help',
        kKeepArrangedIcon             : 'arng',
        kLockedIcon                   : 'lock',
        kNoFilesIcon                  : 'nfil',
        kNoFolderIcon                 : 'nfld',
        kNoWriteIcon                  : 'nwrt',
        kProtectedApplicationFolderIcon : 'papp',
        kProtectedSystemFolderIcon    : 'psys',
        kRecentItemsIcon              : 'rcnt',
        kShortcutIcon                 : 'shrt',
        kSortAscendingIcon            : 'asnd',
        kSortDescendingIcon           : 'dsnd',
        kUnlockedIcon                 : 'ulck',
        kConnectToIcon                : 'cnct',
        kGenericWindowIcon            : 'gwin',
        kQuestionMarkIcon             : 'ques',
        kDeleteAliasIcon              : 'dali',
        kEjectMediaIcon               : 'ejec',
        kBurningIcon                  : 'burn',
        kRightContainerArrowIcon      : 'rcar'
      ]
    
      
    
}
