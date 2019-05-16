from autofocus_class import autofocus
af = autofocus()

i=0
while(True):
    test_rad = af.main()
    print test_rad
    
af.camera_close()
