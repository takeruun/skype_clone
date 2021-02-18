import 'package:flutter/material.dart';
import 'package:skype_clone/enum/view_state.dart';

class ImageUploadProvider extends ChangeNotifier {
  ViewState _viewSatate = ViewState.IDLE;
  ViewState get getViewState => _viewSatate;

  void setToLoading() {
    _viewSatate = ViewState.LOADING;
    notifyListeners();
  }

  void setToIdle() {
    _viewSatate = ViewState.IDLE;
    notifyListeners();
  }
}
