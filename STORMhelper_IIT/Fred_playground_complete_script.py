import numpy as np
import os
import PySpin
import matplotlib.pyplot as plt
import cv2
import math as maths
import math
import random as random
import scipy.ndimage
import scipy.signal
from scipy import optimize

NUM_IMAGES = 10  # number of images to grab
num_lines = 20
os.chdir('C:/Program Files/Micro-Manager-2.0beta')
#import MMCorePy
z_stage_positions = []
start_z_position = 8869.80
range_z = 100.00
end_z_position = start_z_position + range_z
tiff_stack_list = []
#mmc = MMCorePy.CMMCore()
#print mmc.getVersionInfo()
#mmc.loadSystemConfiguration('Olympus_z_drive.cfg')
#mmc.setProperty('ManualFocus', 'Position', start_z_position)
#mmc.waitForDevice('ManualFocus')
y0, y_bottom, y_top = 830, 830, 830
x0, x_bottom, x_top = 970, 970, 970
kernel = np.ones((10,10),np.uint8)
radius = 800
ang_range_deg = 120
showlines=0
peakfind_fignum = 0#diagnostic fig number for peak finding - does stuff if >0
diagnostics = peakfind_fignum+0



def give_radials(x_in,y_in,num_angles,radius,direction):
    if(direction==1):# R
        start_angle = 0-(ang_range_deg/2)
    elif(direction==2):#L
        start_angle = 180-(ang_range_deg/2)
    elif(direction==3):#DOWN
        start_angle = 90-(ang_range_deg/2)
    else:#DEFAULT TO 0 = UP
        start_angle = 270-(ang_range_deg/2)
    base_angle = maths.radians(start_angle)
    delta_angle = maths.radians(ang_range_deg/(num_angles-1))
    Angles = [(base_angle+(delta_angle*(x)))for x in range(0,num_angles)]
    x_array = [x0+radius*maths.cos(angle) for angle in Angles]
    y_array = [y0+radius*maths.sin(angle) for angle in Angles]        
    return x_array,y_array

def acquire_and_display_images(cam, nodemap, nodemap_tldevice):
    """
    This function acquires and displays the channel statistics of N images from a device.

    :param cam: Camera to acquire images from.
    :param nodemap: Device nodemap.
    :param nodemap_tldevice: Transport layer device nodemap.
    :type cam: CameraPtr
    :type nodemap: INodeMap
    :type nodemap_tldevice: INodeMap
    :return: True if successful, False otherwise.
    :rtype: bool
    """

    print '*** IMAGE ACQUISITION ***\n'
    count=-40
    try:
        result = True

        node_acquisition_mode = PySpin.CEnumerationPtr(nodemap.GetNode('AcquisitionMode'))
        if not PySpin.IsAvailable(node_acquisition_mode) or not PySpin.IsWritable(node_acquisition_mode):
            print 'Unable to set acquisition mode to continuous (enum retrieval). Aborting...'
            return False

        # Retrieve entry node from enumeration node
        node_acquisition_mode_continuous = node_acquisition_mode.GetEntryByName('Continuous')
        if not PySpin.IsAvailable(node_acquisition_mode_continuous) or not PySpin.IsReadable(
                node_acquisition_mode_continuous):
            print 'Unable to set acquisition mode to continuous (entry retrieval). Aborting...'
            return False

        # Retrieve integer value from entry node
        acquisition_mode_continuous = node_acquisition_mode_continuous.GetValue()

        # Set integer value from entry node as new value of enumeration node
        node_acquisition_mode.SetIntValue(acquisition_mode_continuous)

        print 'Acquisition mode set to continuous...'

        node_pixel_format = PySpin.CEnumerationPtr(nodemap.GetNode('PixelFormat'))
        if not PySpin.IsAvailable(node_pixel_format) or not PySpin.IsWritable(node_pixel_format):
            print 'Unable to set Pixel Format. Aborting...'
            return False

        else:
            # Retrieve entry node from enumeration node
            node_pixel_format_mono8 = PySpin.CEnumEntryPtr(node_pixel_format.GetEntryByName('Mono8'))
            if not PySpin.IsAvailable(node_pixel_format_mono8) or not PySpin.IsReadable(node_pixel_format_mono8):
                print 'Unable to set Pixel Format to MONO8. Aborting...'
                return False

            # Retrieve integer value from entry node
            pixel_format_mono8 = node_pixel_format_mono8.GetValue()

            # Set integer value from entry node as new value of enumeration node
            node_pixel_format.SetIntValue(pixel_format_mono8)

            print 'Pixel Format set to MONO8 ...'

        cam.BeginAcquisition()

        print 'Acquiring images...'

        device_serial_number = '17497201'
        '''
        node_device_serial_number = PySpin.CStringPtr(nodemap_tldevice.GetNode('DeviceSerialNumber'))
        if PySpin.IsAvailable(node_device_serial_number) and PySpin.IsReadable(node_device_serial_number):
            device_serial_number = node_device_serial_number.GetValue()
            print 'Device serial number retrieved as %s...' % device_serial_number
        '''
        #plt.ion()
        current_z_position = start_z_position
        counting_ = 0
        image_width = 0
        image_height = 0
        while True:
            #z_stage_positions.append(current_z_position)
            #if round(z_stage_positions[-1],1) == round(end_z_position,1):
                #break
            if counting_ == 800*10: #FREDDD
                break
            counting_ += 1
        #for i in range(NUM_IMAGES):
            try:
                image_result = cam.GetNextImage()

                if image_result.IsIncomplete():
                    print 'Image incomplete with image status %d ...' % image_result.GetImageStatus()
                else:
                    #fig = plt.figure(1)
                    print 'dffg'

                try:
                    image_stats = image_result.CalculateChannelStatistics(PySpin.GREY)
                    # Getting the image data as a numpy array
                    image_data = image_result.GetNDArray()
                    #plt.imshow(image_data, cmap='gray')

                    # Show the image
                    #plt.show()
                    #plt.pause(0.01)
                    ellipse_x = []
                    ellipse_y = []
                    
                    if image_width == 0:
                        image_width = np.size(image_data)/len(image_data)
                        image_height = len(image_data)
                    semicircle = image_data
                    image = image_data
                    cv2.normalize(semicircle,semicircle,0,255,cv2.NORM_MINMAX)
                    #print(np.amax(data))
                    '''
                    plt.figure(235)
                    plt.set_cmap('gray')
                    plt.scatter(x0,y0)
                    plt.imshow(semicircle)
                    '''

                    img_semicircle = semicircle.astype(np.uint8)
                    thresh_x_range = range(image_width-100,image_width,1)
                    thresh_x_range1 = range(0,100,1)
                    new_x_range = range(x0-50,x0+50,1)
                    thresh_min = np.amax(image[:,thresh_x_range1][:100])
                    #thresh_min = 30
                    #print thresh_min
                    #thresh_min = sort(image[:,thresh_x_range][:50].ravel())[int(round(0.70*len(image[:,thresh_x_range][:50].ravel())))]
                    #print thresh_min
                    #thresh_min = np.amax(image[:,thresh_x_range][900:])
                    thresh_mean = np.mean(image[:,thresh_x_range][:200])
                    thresh_median = np.median(image[:,thresh_x_range][900:])
                    thresh_mode1 = scipy.stats.mode(image[:,thresh_x_range1][:200], axis=None)[0][0] +1 
                    thresh_mode = scipy.stats.mode(image[:,thresh_x_range][:200], axis=None)[0][0] +1
                    thresh_mode2 = scipy.stats.mode(image[:,new_x_range][1:101], axis=None)[0][0]
                    thresh_mode3 = scipy.stats.mode(image[:,new_x_range][750:850], axis=None)[0][0]
                    thresh_mode_up = np.mean(image[:,new_x_range][1:101])
                    thresh_mode_down = np.mean(image[:,new_x_range][750:850])
                    if thresh_mode_up <= thresh_mode_down:
                        used_thresh_mode = thresh_mode
                    else:
                        used_thresh_mode = thresh_min
                    #print used_thresh_mode
                    #print thresh_min, thresh_mode, thresh_mode1, thresh_median, thresh_mean
                    #print thresh_mode2, thresh_mode3
                    new_semicircle = cv2.threshold(img_semicircle,thresh_min,255,cv2.THRESH_BINARY)
                    #opening = semicircle
                    opening = new_semicircle[1]
                    opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, kernel)
                    opening[0] = 0
                    opening[:, 0] = 0
                    inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
                    h, w = inverse_binary[1].shape[:2]
                    mask = np.zeros((h+2, w+2), np.uint8)
                    
                    floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
                    #print floodfill
                    #combined_image = opening + floodfill[1]
                    combined_image = opening + inverse_binary[1]
                    
                    '''
                    plt.figure(79)
                    plt.set_cmap('gray')
                    plt.imshow(combined_image)
                    plt.scatter(x0,y0)
                    plt.show()
                    '''
                    
                    combined_image2, label = CC(combined_image, img_semicircle, thresh_min, thresh_mode)
                    '''
                    plt.figure(76)
                    plt.set_cmap('gray')
                    plt.imshow(combined_image2)
                    plt.scatter(x0,y0)
                    plt.show()
                    '''
                    combined_image3 = np.uint8(combined_image2)
                    scaled_down_image = combined_image3
                    major = cv2.__version__.split('.')[0]
                    if major == '3':
                        gsxgxg, contours,hierarchy = cv2.findContours(scaled_down_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
                    
                    else:
                        contours,hierarchy = cv2.findContours(scaled_down_image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
                    cnt = contours
                    if cnt != []:
                        hull = cv2.convexHull(np.array(cnt[-1]))
                        mask2 = np.zeros(combined_image.shape[:2], np.uint8)
                        contour_image = cv2.drawContours(mask2, [hull], -1, 255, -1 )
                        contour_image = mask2
                    else:
                        combined_image2[combined_image2>255] = 255
                        contour_image = combined_image2
                    data = contour_image
                    '''
                    plt.figure(76)
                    plt.set_cmap('gray')
                    plt.imshow(data)
                    plt.scatter(x0,y0)
                    plt.show()
                    '''
                    right_range = range(x0,np.size(data)/len(data),1)
                    left_range = range(0,x0,1)
                    right_sum = np.sum(data[:,right_range])
                    left_sum = np.sum(data[:,left_range])

                    if right_sum == 0 and left_sum == 0:
                        right_sum = np.sum(data[:,right_range])
                        left_sum = np.sum(data[:,left_range])
                    
                    area = np.count_nonzero(contour_image)
                    area_sqrt = np.sqrt(area)
                    

                    area1 = np.count_nonzero(combined_image2)
                    area_sqrt1 = np.sqrt(area1)


                    
                    #plt.figure(3)
                    #plt.set_cmap('gray')
                    #plt.imshow(contour_image)
                    #plt.scatter(x0,y0,color='green')
                    #plt.show()


                    
                    #data = image
                    #print(type(data))

                    if left_sum > right_sum:
                        x_array,y_array = give_radials(x0,y0,num_lines,radius,2)
                    else:
                        x_array,y_array = give_radials(x0,y0,num_lines,radius,1)

                    for idx in range (0,len(y_array)):

                        num = pow((pow((x_array[idx]-x0),2)+pow((y_array[idx]-y0),2)),0.5)
                        #Define start, endpoint arrays
                        xQ,yQ =  np.linspace(x0, x_array[idx], int(num)), np.linspace(y0, y_array[idx], int(num))
                        #xQ,yQ =  np.linspace(0, 800, 1024), np.linspace(600+(10*idx), 700+(10*idx), int(1024))
                        #Measure along line
                        zi = data[yQ.astype(np.int), xQ.astype(np.int)]
                        #zi_2 = semicircle[yQ.astype(np.int), xQ.astype(np.int)]
                        #print zi
                        #zi = scipy.ndimage.interpolation.map_coordinates(data, np.vstack((yQ,xQ)))
                        #if (showlines>0):
                            #plt.plot(zi)
                        maxloc = find_peaks(zi,peakfind_fignum)
                        
                        #shit_maxloc = find_peaks(zi_2,peakfind_fignum)
                        #plt.figure(3)
                        #plt.scatter(x0,y0, color = 'r')
                        #plt.scatter(xQ[maxloc],yQ[maxloc], color = 'b')
                        ellipse_x.append(xQ[maxloc])
                        ellipse_y.append(yQ[maxloc])


                    #### Filter values that seem off - RELIES ON SENSIBLE CENTRE! ####
                    rad_dist = [val*0 for val in ellipse_x]
                    for k in range (0, len(ellipse_x)-1):
                        rad_dist[k] = math.sqrt(math.pow((ellipse_x[k]-x0),2)+math.pow((ellipse_y[k]-y0),2))
                    '''
                    shit_rad_dist = [val*0 for val in shit_ellipse_x]
                    for k in range (0, len(shit_ellipse_x)-1):
                        shit_rad_dist[k] = math.sqrt(math.pow((shit_ellipse_x[k]-x0),2)+math.pow((shit_ellipse_y[k]-y0),2))
                    '''
                    r_d = np.array(rad_dist)
                    for i in range(len(r_d)):
                        if r_d[i] > 0 and r_d[i] < 20:
                            r_d[i] = 800
                        elif r_d[i] < 0 and r_d[i] <20:
                            r_d[i] = -800
                    #print r_d
                    std = np.std(r_d)
                    mean = np.mean(r_d)
                    #print(r_d)
                    #print(std)
                    std_adj = std*0.25

                    good_Rs = [val for val in r_d if val>(mean-std) and val <(mean+std)]
                
                    if left_sum > right_sum:
                        calc_radius = np.mean(good_Rs)  
                        #shit_radii_list.append(np.mean(shit_good_Rs))
                        #radii_list.append(np.mean(good_Rs))
                        #print np.mean(good_Rs), "radius"
                        #calc_radius = np.mean(good_Rs)       
                    else:
                        calc_radius = -1*np.mean(good_Rs)
                        #radii_list.append(-1*np.mean(good_Rs))
                        #shit_radii_list.append(-1*np.mean(shit_good_Rs))
                        #print -1*np.mean(good_Rs), "radius"
                                        # Using matplotlib, two subplots are created where the top subplot is the histogram and the 
                    # bottom subplot is the image.
                    # 
                    # Refer to https://matplotlib.org/2.0.2/api/pyplot_api.html#module-matplotlib.pyplot
                    #print calc_radius
                    area_check = 'no'
                    if calc_radius < 0 and calc_radius >= -320:
                        gradient = 29.56722
                        intercept = -116.47194
                    elif calc_radius >= 0 and calc_radius <= 560:
                        gradient = 28.30885
                        intercept = -72.76849
                    elif calc_radius < -320:
                        area_check = 'yes'
                        gradient = -7.41688
                        intercept = 403.27373
                    else:
                        area_check = 'yes'
                        gradient = -4.26632
                        intercept = -600.04073

                    calc_defocus = round((calc_radius-intercept)/gradient,1)
                     #print calc_defocus

                     #FREDDD
                     #np.savetxt('C:/Program Files/Micro-Manager-2.0beta/zPosSTORM.txt',np.array([calc_defocus]), fmt = '%.4f')
                    if count < 40: 
                        count = count+0.1
                    else:
                        count = -40

                    print count   
                    np.savetxt('C:/Program Files/Micro-Manager-2.0beta/zPosSTORM.txt',np.array([count]), fmt = '%.4f') 
      
                    #plt.cla()
                    plt.imshow(image_data, cmap='gray')

                    # Show the image
                    #plt.show()
                    #plt.pause(0.01)

                    #current_z_position = move_z_stage_storm(0.50)
                    #tiff_stack_list.append(np.uint8(image_data))

                except PySpin.SpinnakerException:
                    raise

                #  Release image
                #
                #  *** NOTES ***
                #  Images retrieved directly from the camera (i.e. non-converted
                #  images) need to be released in order to keep from filling the
                #  buffer.
                image_result.Release()

            except PySpin.SpinnakerException:
                raise

        cam.EndAcquisition()
        print 'End Acquisition'

        plt.close()

    except PySpin.SpinnakerException as ex:
        print 'Error: %s' % ex
        return False

    return result

def configure_exposure(cam):
    """
     This function configures a custom exposure time. Automatic exposure is turned
     off in order to allow for the customization, and then the custom setting is
     applied.

     :param cam: Camera to configure exposure for.
     :type cam: CameraPtr
     :return: True if successful, False otherwise.
     :rtype: bool
    """

    print '*** CONFIGURING EXPOSURE ***\n'

    try:
        result = True

        # Turn off automatic exposure mode
        #
        # *** NOTES ***
        # Automatic exposure prevents the manual configuration of exposure
        # times and needs to be turned off for this example. Enumerations
        # representing entry nodes have been added to QuickSpin. This allows
        # for the much easier setting of enumeration nodes to new values.
        #
        # The naming convention of QuickSpin enums is the name of the
        # enumeration node followed by an underscore and the symbolic of
        # the entry node. Selecting "Off" on the "ExposureAuto" node is
        # thus named "ExposureAuto_Off".
        #
        # *** LATER ***
        # Exposure time can be set automatically or manually as needed. This
        # example turns automatic exposure off to set it manually and back
        # on to return the camera to its default state.
        print cam.ExposureTime.GetValue(), 'exposure before'
        if cam.ExposureAuto.GetAccessMode() != PySpin.RW:
            print 'Unable to disable automatic exposure. Aborting...'
            return False
      
        cam.ExposureAuto.SetValue(PySpin.ExposureAuto_Off)
        print 'Automatic exposure disabled...'


        print cam.Gain.GetValue(), 'gain before'
        if cam.GainAuto.GetAccessMode() != PySpin.RW:
            print 'Unable to disable automatic gain. Aborting...'
            return False

        cam.GainAuto.SetValue(PySpin.GainAuto_Off)
        print 'Automatic Gain disabled...'


        

        # Set exposure time manually; exposure time recorded in microseconds
        #
        # *** NOTES ***
        # Notice that the node is checked for availability and writability
        # prior to the setting of the node. In QuickSpin, availability and
        # writability are ensured by checking the access mode.
        #
        # Further, it is ensured that the desired exposure time does not exceed
        # the maximum. Exposure time is counted in microseconds - this can be
        # found out either by retrieving the unit with the GetUnit() method or
        # by checking SpinView.

        if cam.ExposureTime.GetAccessMode() != PySpin.RW:
            print 'Unable to set exposure time. Aborting...'
            return False

        # Ensure desired exposure time does not exceed the maximum
        exposure_time_to_set = 30000.0
        exposure_time_to_set = min(cam.ExposureTime.GetMax(), exposure_time_to_set)
        cam.ExposureTime.SetValue(exposure_time_to_set)
        print cam.ExposureTime.GetValue(), 'exposure after'

        if cam.Gain.GetAccessMode() != PySpin.RW:
            print 'Unable to set Gain. Aborting...'
            return False
        
        gain_to_set = 0.00
        gain_to_set = min(cam.Gain.GetMax(), gain_to_set)
        cam.Gain.SetValue(gain_to_set)
        print cam.Gain.GetValue(), 'gain after'

    except PySpin.SpinnakerException as ex:
        print 'Error: %s' % ex
        result = False

    return result


def reset_exposure(cam):
    """
    This function returns the camera to a normal state by re-enabling automatic exposure.

    :param cam: Camera to reset exposure on.
    :type cam: CameraPtr
    :return: True if successful, False otherwise.
    :rtype: bool
    """
    try:
        result = True

        # Turn automatic exposure back on
        #
        # *** NOTES ***
        # Automatic exposure is turned on in order to return the camera to its
        # default state.

        if cam.ExposureAuto.GetAccessMode() != PySpin.RW:
            print 'Unable to enable automatic exposure (node retrieval). Non-fatal error...'
            return False

        if cam.GainAuto.GetAccessMode() != PySpin.RW:
            print 'Unable to enable automatic gain (node retrieval). Non-fatal error...'
            return False

        cam.ExposureAuto.SetValue(PySpin.ExposureAuto_Continuous)
        cam.GainAuto.SetValue(PySpin.GainAuto_Continuous)

        print 'Automatic exposure enabled...'

    except PySpin.SpinnakerException as ex:
        print 'Error: %s' % ex
        result = False

    return result


def print_device_info(cam):
    """
    This function prints the device information of the camera from the transport
    layer; please see NodeMapInfo example for more in-depth comments on printing
    device information from the nodemap.

    :param cam: Camera to get device information from.
    :type cam: CameraPtr
    :return: True if successful, False otherwise.
    :rtype: bool
    """

    print '*** DEVICE INFORMATION ***\n'

    try:
        result = True
        nodemap = cam.GetTLDeviceNodeMap()

        node_device_information = PySpin.CCategoryPtr(nodemap.GetNode('DeviceInformation'))

        if PySpin.IsAvailable(node_device_information) and PySpin.IsReadable(node_device_information):
            features = node_device_information.GetFeatures()
            for feature in features:
                node_feature = PySpin.CValuePtr(feature)
                print '%s: %s' % (node_feature.GetName(),
                                  node_feature.ToString() if PySpin.IsReadable(node_feature) else 'Node not readable')

        else:
            print 'Device control information not available.'

    except PySpin.SpinnakerException as ex:
        print 'Error: %s' % ex.message
        return False

    return result


def run_single_camera(cam):
    """
    This function acts as the body of the example; please see NodeMapInfo example
    for more in-depth comments on setting up cameras.

    :param cam: Camera to run on.
    :type cam: CameraPtr
    :return: True if successful, False otherwise.
    :rtype: bool
    """
    try:
        result = True

        nodemap_tldevice = cam.GetTLDeviceNodeMap()

        #Initialize camera
        cam.Init()

       # Print device info
        result = print_device_info(cam)

        # Configure exposure
        if not configure_exposure(cam):
            return False        

        # Retrieve GenICam nodemap
        nodemap = cam.GetNodeMap()

        # Acquire images
        result &= acquire_and_display_images(cam, nodemap, nodemap_tldevice)

        # Deinitialize camera
        cam.DeInit()

    except PySpin.SpinnakerException as ex:
        print 'Error: %s' % ex
        result = False

    return result

def move_z_stage_storm(stepsize):

    current_position = mmc.getProperty('ManualFocus','Position')
    #print current_position
    new_position = format(np.float64(current_position)+stepsize, '.4f')
    #print new_position
    #mmc.setProperty('ManualFocus', 'Position', new_position)
    #mmc.waitForDevice('ManualFocus')
    final_position = np.float64(mmc.getProperty('ManualFocus','Position'))
    print final_position
    return final_position

def find_peaks(line,fignum):
    np_line = np.array(line)
    #print np.nonzero(np_line)[0]
    if np.nonzero(np_line)[0] != []:
      if np_line[0] == 0:
        for i in range(np.nonzero(np_line)[0][0]):
            np_line[i] = 255
    peak_thresh = (0.2*(max(np_line)-min(np_line)))+min(np_line)
    #print peak_thresh
    low_bits = np_line < peak_thresh
    np_line[low_bits]=min(np_line)
    #print np_line
    #window is 21, order is 4 - see if this applies everywhere - maybe scale it to blob size?
    window = 21
    order = 4
    
    smooth = savitzky_golay(np_line, window, order)
    #print smooth
    #print smooth
    indices = [4]
    max_ = np.amax(smooth)
    if max_ == 255.0000000000022:
        indices.append(len(smooth)-1)
    else:    
     for i in range(0, len(smooth)):
        #print smooth[i]
        if smooth[i] == max_:
        #if smooth[i] == 271.29846355017787:
            indices.append(i)
            
    #print np.asarray(indices)        
    #indices = scipy.signal.find_peaks_cwt(smooth,[window/5])
    #print indices
    #print (time.time() - start_time)
    #print indices
    #valatind = smooth[indices]
    #print valatind
    #print valatind
    #valatind = np.array([255. ,        271.29846355017787])
    valatind = np.array([255. ,        max_])
    #print valatind
    #print (time.time() - start_time)
    #print valatind
    #print smooth
    new_indices = []
    new_valatind = []
    ### Reconsider use of peak_threshold here - something higher maybe?
    for ctr in range(0,len(valatind-1)):
        if valatind[ctr]>peak_thresh:
            new_indices.append(indices[ctr])
            new_valatind.append(valatind[ctr])
    #print new_valatind
    #Easy fit into old framework
    vp = max(new_indices)

    if(fignum>0):
        prev_fig = plt.gcf()
        plt.figure(fignum)
        plt.plot(np_line)
        plt.figure(prev_fig.number)
    return vp

def savitzky_golay(y, window_size, order, deriv=0, rate=1):
    
    from math import factorial
    
    try:
        window_size = np.abs(np.int(window_size))
        order = np.abs(np.int(order))
    except (ValueError, msg):
        raise ValueError("window_size and order have to be of type int")
    if window_size % 2 != 1 or window_size < 1:
        raise TypeError("window_size size must be a positive odd number")
    if window_size < order + 2:
        raise TypeError("window_size is too small for the polynomials order")
    order_range = range(order+1)
    half_window = (window_size -1) // 2
    # precompute coefficients
    b = np.mat([[k**i for i in order_range] for k in range(-half_window, half_window+1)])
    m = np.linalg.pinv(b).A[deriv] * rate**deriv * factorial(deriv)
    # pad the signal at the extremes with
    # values taken from the signal itself
    firstvals = y[0] - np.abs( y[1:half_window+1][::-1] - y[0] )
    lastvals = y[-1] + np.abs(y[-half_window-1:-1][::-1] - y[-1])
    y = np.concatenate((firstvals, y, lastvals))
    return np.convolve( m[::-1], y, mode='valid')

##########################################

def CC(Map, img_semicircle,thresh_min ,thresh_mode):
    shape = np.shape(Map)
    label_img, cc_num = scipy.ndimage.label(Map)
    '''
    plt.figure(1)
    plt.set_cmap('gray')
    plt.imshow(label_img)
    '''
    
    cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
    largest_area = max(cc_areas)/10
    #print largest_area*10
    #for cc_area in cc_areas:
        #if cc_area < largest_area/10:
            #cc_area = 0
    area_mask = (cc_areas < largest_area)
    label_img[area_mask[label_img]] = 0
    '''
    plt.figure(2)
    plt.imshow(label_img)
    plt.show()
    '''
    
    #print CC
    useful_CC = []
    #cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
    #largest_area = max(cc_areas)/10
    #print largest_area*10
    #area_mask = (cc_areas < largest_area)
    #label_img[area_mask[label_img]] = 0
    CC = scipy.ndimage.find_objects(label_img)
    #print CC
    for slices in CC:
      if slices != None:  
        #print slices
        if slices[0].start <= y_bottom and slices[0].stop >= y_bottom and slices[1].start <= x_bottom and slices[1].stop >= x_bottom:
            useful_CC.append(slices)
        elif slices[0].start <= y_top and slices[0].stop >= y_top and slices[1].start <= x_top and slices[1].stop >= x_top:
            useful_CC.append(slices)
     
    #print useful_CC
    if CC == [] or largest_area*10 < 1000000:# or useful_CC == []:
        new_semicircle = cv2.threshold(img_semicircle,thresh_mode,255,cv2.THRESH_BINARY)
        opening = new_semicircle[1]
        opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, kernel)
        inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
        h, w = inverse_binary[1].shape[:2]
        mask = np.zeros((h+2, w+2), np.uint8)
        floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
        combined_image = opening + inverse_binary[1]
        shape = np.shape(combined_image)
        label_img, cc_num = scipy.ndimage.label(combined_image)
        CC = scipy.ndimage.find_objects(label_img)
        #print CC
        useful_CC = []
        for slices in CC:
            if slices[0].start <= y_bottom and slices[0].stop >= y_bottom and slices[1].start <= x_bottom and slices[1].stop >= x_bottom:
                useful_CC.append(slices)
            elif slices[0].start <= y_top and slices[0].stop >= y_top and slices[1].start <= x_top and slices[1].stop >= x_top:
                useful_CC.append(slices)
        cc_areas = scipy.ndimage.sum(combined_image, label_img, range(cc_num+1))
    elif useful_CC == [(slice(0L, 1024L, None), slice(0L, 1280L, None))]:
        new_semicircle = cv2.threshold(img_semicircle,thresh_min,255,cv2.THRESH_BINARY)
        opening = new_semicircle[1]
        opening = cv2.morphologyEx(opening, cv2.MORPH_OPEN, kernel)
        inverse_binary = cv2.threshold(opening,60,255.0,cv2.THRESH_BINARY_INV)
        h, w = inverse_binary[1].shape[:2]
        mask = np.zeros((h+2, w+2), np.uint8)
        floodfill = cv2.floodFill(inverse_binary[1], mask, (0,0), 0)
        combined_image = opening + inverse_binary[1]
        shape = np.shape(combined_image)
        label_img, cc_num = scipy.ndimage.label(combined_image)
        CC = scipy.ndimage.find_objects(label_img)
        #print CC
        useful_CC = []
        for slices in CC:
            if slices[0].start <= y_bottom and slices[0].stop >= y_bottom and slices[1].start <= x_bottom and slices[1].stop >= x_bottom:
                useful_CC.append(slices)
            elif slices[0].start <= y_top and slices[0].stop >= y_top and slices[1].start <= x_top and slices[1].stop >= x_top:
                useful_CC.append(slices)
        cc_areas = scipy.ndimage.sum(combined_image, label_img, range(cc_num+1))        
    else:
        cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
    #print cc_areas, max(cc_areas)

        
    #largest_area = max(cc_areas)
    #area_mask = (cc_areas < largest_area)
    #print area_mask
    #print useful_CC
    if np.size(useful_CC)> 2:
        CC_0_size = (useful_CC[0][0].stop-useful_CC[0][0].start)*(useful_CC[0][1].stop-useful_CC[0][1].start)
        CC_1_size = (useful_CC[1][0].stop-useful_CC[1][0].start)*(useful_CC[1][1].stop-useful_CC[1][1].start)
        if CC_0_size >= CC_1_size:
            useful_CC = [useful_CC[0]]
        else:
            useful_CC = [useful_CC[1]]
    #print useful_CC
    area_mask2 = [True]

    #plt.figure(3)
    #plt.imshow(label_img)
    #plt.show()
    if useful_CC != []:
     for ccs in CC:
        #print ccs, useful_CC[0]
        area_mask1 = (useful_CC[0] != ccs)
        area_mask2.append(area_mask1)

     area_mask2 = np.asarray(area_mask2)
    #print area_mask2
    #area_mask = list(set(CC) - set(temp2)) 
     label_img[area_mask2[label_img]] = 0

    else:
        Map = label_img
        label_img, cc_num = scipy.ndimage.label(Map)
        cc_areas = scipy.ndimage.sum(Map, label_img, range(cc_num+1))
        largest_area = max(cc_areas)
        area_mask = (cc_areas < largest_area)
        label_img[area_mask[label_img]] = 0



    return label_img, CC


    

def main():
    """
    Example entry point; notice the volume of data that the logging event handler
    prints out on debug despite the fact that very little really happens in this
    example. Because of this, it may be better to have the logger set to lower
    level in order to provide a more concise, focused log.

    :return: True if successful, False otherwise.
    :rtype: bool
    """

    # Since this application saves images in the current folder
    # we must ensure that we have permission to write to this folder.
    # If we do not have permission, fail right away.
  

    try:
        test_file = open('test.txt', 'w+')
    except IOError:
        print 'Unable to write to current directory. Please check permissions.'
        raw_input('Press Enter to exit...')
        return False

    test_file.close()
    os.remove(test_file.name)

    result = True

    # Retrieve singleton reference to system object
    system = PySpin.System.GetInstance()

    # Get current library version
    version = system.GetLibraryVersion()
    print 'Library version: %d.%d.%d.%d' % (version.major, version.minor, version.type, version.build)

    # Retrieve list of cameras from the system
    cam_list = system.GetCameras()

    num_cameras = cam_list.GetSize()

    print 'Number of cameras detected: %d' % num_cameras

    # Finish if there are no cameras
    if num_cameras == 0:

        # Clear camera list before releasing system
        cam_list.Clear()

        # Release system instance
        system.ReleaseInstance()

        print 'Not enough cameras!'
        raw_input('Done! Press Enter to exit...')
        return False

    # Run example on each camera
    for i, cam in enumerate(cam_list):

        print 'Running example for camera %d...' % i

        result &= run_single_camera(cam)
        print 'Camera %d example complete... \n' % i

    # Release reference to camera
    # NOTE: Unlike the C++ examples, we cannot rely on pointer objects being automatically
    # cleaned up when going out of scope.
    # The usage of del is preferred to assigning the variable to None.
    del cam

    # Clear camera list before releasing system
    cam_list.Clear()

    # Release system instance
    system.ReleaseInstance()

    raw_input('Done! Press Enter to exit...')
    return result


if __name__ == '__main__':
    main()

